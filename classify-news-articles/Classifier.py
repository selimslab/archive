import math
import os
from FileManager import FileManager
from Stats import Stats
from Evaluator import Evaluator
from DataManager import DataManager, DatasetCreator


class NaiveBayesClassifier(object):
    def __init__(self, dataset, topics):
        self.dataset = dataset
        self.topics = topics
        self.the_most_informative_words = None
        self.size_of_vocabulary = 0
        self.get_data()

    def get_data(self):
        ready = (
            os.path.exists("training.txt")
            and os.path.exists("test.txt")
            and os.path.exists("doc_count_training.txt")
            and os.path.exists("training_vocabulary.txt")
        )

        if ready:
            self.read_data()
        else:
            self.process_data()

    def read_data(self):
        self.training_set = FileManager.read_data_structure("training.txt")
        self.test_set = FileManager.read_data_structure("test.txt")
        self.doc_count_training = FileManager.read_data_structure(
            "doc_count_training.txt"
        )
        self.training_vocabulary = FileManager.read_data_structure(
            "training_vocabulary.txt"
        )

    def process_data(self):
        dm = DataManager(self.topics)
        dm.create_datasets(self.dataset)
        self.training_set = dm.training_set
        self.test_set = dm.test_set
        self.doc_count_training = dm.doc_count_training
        self.training_vocabulary = dm.training_vocabulary
        self.size_of_vocabulary = len(self.training_vocabulary)

    def get_class_probabilities(self):
        self.class_probabilities = {}
        number_of_all_docs = Stats.get_total_doc_count(self.doc_count_training)
        for tag in self.topics:
            class_probability = math.log(
                float(self.doc_count_training[tag]) / number_of_all_docs
            )
            self.class_probabilities[tag] = class_probability

    def get_word_probability(self, word, tag, test_scope):
        if self.alpha <= 0.0001:
            self.size_of_vocabulary = 0

        if word in test_scope[tag]:  # if it is one of the informative words
            number_of_occurrence = test_scope[tag][word]
        else:
            number_of_occurrence = 0

        probability_of_word_given_class = math.log(
            float(number_of_occurrence + self.alpha)
            / float(self.size_of_megadoc + self.size_of_vocabulary)
        )
        return probability_of_word_given_class

    def get_guess_probabilities(self, doc, test_scope):
        guess_probabilities = {}

        for tag in self.topics:
            probability_class_given_doc = 0

            probability_class_given_doc += self.class_probabilities[tag]

            for word in doc:
                probability_class_given_doc += self.get_word_probability(
                    word, tag, test_scope
                )

            guess_probabilities[tag] = probability_class_given_doc

        return guess_probabilities

    def test(self, test_scope):
        ev = Evaluator(self.topics)

        self.get_class_probabilities()

        for tag in self.topics:
            self.size_of_megadoc = Stats.get_megadoc_size(test_scope, tag)
            for doc in self.test_set[tag]:
                guess_probabilities = self.get_guess_probabilities(
                    self.test_set[tag][doc], test_scope
                )
                guess = max(guess_probabilities, key=guess_probabilities.get)
                ev.update_truth_tables(guess, tag)

        for topic in self.topics:
            ev.calculate_scores(topic)
        ev.calculate_averages()
        ev.print_summary(self.alpha)

    def test_using_all_words(self, alpha):
        self.alpha = alpha
        print "using all words.."
        self.size_of_vocabulary = len(self.training_vocabulary)
        test_scope = self.training_set
        self.test(test_scope)

    def test_using_the_most_informative_words(self, alpha, k):
        self.alpha = alpha
        if self.the_most_informative_words is None:
            self.the_most_informative_words = self.find_the_most_informative_words(
                self.training_set, k
            )

        print "using the %d most informative words.." % k

        self.size_of_vocabulary = k * len(self.topics)
        test_scope = self.the_most_informative_words
        self.test(test_scope)
        # self.size_of_vocabulary = k*len(the_most_informative_words)

    def find_the_most_informative_words(self, dataset, k):

        print "finding the most informative words.."
        mutual_informations = DatasetCreator.create_empty_dataset(self.topics)
        the_most_informative_words = DatasetCreator.create_empty_dataset(self.topics)

        for tag in dataset:
            for word in dataset[tag]:
                mutual_informations[tag][word] = self.get_mutual_information(
                    dataset, tag, word
                )

            sorted_by_mi = sorted(
                mutual_informations[tag], key=mutual_informations[tag].get, reverse=True
            )

            for i in range(0, k):
                word = sorted_by_mi[i]
                the_most_informative_words[tag][word] = dataset[tag][word]

        FileManager.write_file(
            "the_most_informative_words.txt", str(the_most_informative_words)
        )
        return the_most_informative_words

    @staticmethod
    def get_mutual_information(dataset, tag, word):

        N = Stats.get_total_word_count(dataset)
        word_total = Stats.get_count_of_a_word_in_all_classes(dataset, word)
        tag_total = len(dataset[tag])

        n11 = dataset[tag][word]  # word_in_tag
        n10 = word_total - n11  # word_except_tag
        n01 = tag_total - n11  # tag_except_word
        n00 = N - tag_total - n10  # neither

        p11 = float(n11 * math.log(float(n11) * N / (n11 + n10) * (n01 + n11) + N, 2))

        p10 = float(n10 * math.log(float(n10) * N / (n11 + n10) * (n10 + n00) + N, 2))

        p01 = float(n01 * math.log(float(n01) * N / (n01 + n00) * (n01 + n11) + N, 2))

        p00 = float(n00 * math.log(float(n00) * N / (n00 + n01) * (n00 + n10) + N, 2))

        mi = float(p11 + p10 + p01 + p00)

        return mi
