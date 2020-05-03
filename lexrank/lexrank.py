import os
import re
import math
import numpy as np

from story_manager import StoryManager
from file_manager import FileManager
from evaluator import Evaluator

from collections import Counter

class Summarizer(object):
    def __init__(self, dataset_path):
        self.dataset_path = dataset_path
        self.summaries = {}
        self.evaluator = Evaluator()
        self.doc_names = os.listdir(self.dataset_path)
        self.idf_scores = self.get_idf_scores(self.doc_names)

    def generate_all_summaries(self):
        for i, doc_name in enumerate(self.doc_names):
            summary, gold_summary = self.summarize(doc_name)
            self.evaluator.calculate_rouge(summary, gold_summary, doc_name)
        self.evaluator.get_avg_rouges()

    def get_idf_scores(self, filenames):
        if os.path.exists("idf_scores.txt"):
            idf_scores = FileManager.read_data_structure("idf_scores.txt")
            return idf_scores
        else:
            return self.calculate_idf(filenames)

    def summarize(self, filename):
        filepath = os.path.join(self.dataset_path, filename)
        story, gold_summary = StoryManager.get_story_and_summary(filepath)
        lexrank_scores = self.calculate_lexrank_scores(story)
        print(lexrank_scores)

        sorted_scores = np.argsort(lexrank_scores)[::-1]
        summary_size = len(gold_summary)
        summary = [story[i] for i in sorted_scores[:summary_size]]
        self.summaries[filename] = summary

        return summary, gold_summary



    def calculate_lexrank_scores(self, story):

        tf_scores = self.calculate_tf(story)

        cosine_similarity_graph = self.create_cosine_similarity_graph(tf_scores)

        markov_matrix = self.apply_teleportation_and_threshold(cosine_similarity_graph)

        scores = self.power_method(markov_matrix)

        return scores

    def power_method(self, matrix):
        error_tolerance = 0.00001
        MAX_ITERATION = 10000
        N = len(matrix)
        p0 = np.full(N, 1.0 / N)

        for i in range(MAX_ITERATION):
            p1 = np.dot(matrix.T, p0)
            if np.allclose(p0, p1) < error_tolerance:
                break
            else:
                p0 = p1
        return p1

    def create_cosine_similarity_graph(self, tf_scores):

        length = len(tf_scores)

        cosine_similarity_graph = np.zeros([length] * 2)

        for i, sentence1 in enumerate(tf_scores):
            for j, sentence2 in enumerate(tf_scores):
                similarity = self.calculate_cosine_similarity(sentence1, sentence2)

                if similarity:
                    cosine_similarity_graph[i, j] = similarity
                    cosine_similarity_graph[j, i] = similarity

        return cosine_similarity_graph

    def get_bags_of_words(self, filenames):
        bags_of_words = []

        for filename in filenames:
            doc_words = set()

            filepath = os.path.join(self.dataset_path, filename)
            story, gold_summary_sentences = StoryManager.get_story_and_summary(filepath)

            for sentence in story:
                words = self.tokenize_sentence(sentence)
                doc_words.update(words)

            if doc_words:
                bags_of_words.append(doc_words)

        return bags_of_words

    def calculate_idf(self, documents):

        bags_of_words = self.get_bags_of_words(documents)
        doc_number_total = len(bags_of_words)

        idf_scores = {}

        for word in set.union(*bags_of_words):  # distinct words in all sets
            doc_frequency = 0
            for bag in bags_of_words:
                if word in bag:
                    doc_frequency += 1
            idf_scores[word] = math.log(doc_number_total / doc_frequency)

        FileManager.write_file("idf_scores.txt", str(idf_scores))

        return idf_scores








    def idf(docs):
        vocabulary = set() 
        counts = Counter()
        for doc in docs:
            tokens = tokenize(doc)
            unique_words = set(tokens)
            vocabulary.update(unique_words)



        idf_scores = {}
        number_of_docs = len(docs)
        for word in vocabulary:
            doc_frequency = 0
            for bag in bags_of_words:
                if word in bag:
                    doc_frequency += 1
            idf_scores[word] = math.log(doc_number_total / doc_frequency)






    def tf(doc):
        sentences = doc.split()


    def calculate_tf(self, story):
        tf_scores = []

        for sentence in story:
            words_of_the_sentence = self.tokenize_sentence(sentence)

            sentence_vocabulary = {}
            for word in words_of_the_sentence:
                if word in sentence_vocabulary:
                    sentence_vocabulary[word] += 1
                else:
                    sentence_vocabulary[word] = 1

            tf_scores.append(sentence_vocabulary)

        return tf_scores

    def tokenize_sentence(self, sentence):
        tokens = re.sub("[^\w]+", " ", sentence.lower()).split()
        return tokens

    def calculate_cosine_similarity(self, sentence1, sentence2):
        if sentence1 == sentence2:
            return 1

        nominator = vec1_norm = vec2_norm = 0
        words1, words2 = set(sentence1.keys()), set(sentence2.keys())

        for word in words1 & words2:
            idf = self.idf_scores[word]
            nominator += sentence1[word] * sentence2[word] * idf ** 2

        if math.isclose(nominator, 0):
            return 0

        for word in sentence1:
            tfidf = sentence1[word] * self.idf_scores[word]
            vec1_norm += tfidf ** 2

        for word in sentence2:
            tfidf = sentence2[word] * self.idf_scores[word]
            vec2_norm += tfidf ** 2

        denominator = math.sqrt(vec1_norm) * math.sqrt(vec2_norm)

        similarity = float(nominator) / denominator

        return similarity

    def apply_teleportation_and_threshold(self, cosine_matrix):

        similarity_threshold = 0.1

        B = np.zeros(cosine_matrix.shape)

        N = len(cosine_matrix)

        for i in range(N):
            columns = np.where(cosine_matrix[i] > similarity_threshold)[0]
            B[i, columns] = 1 / len(columns)

        U = np.full(cosine_matrix.shape, 1 / N)
        d = 0.15 # teleportation_rate
        markov_matrix = np.dot(d, U) + np.dot((1 - d), B)

        return markov_matrix




def lexrank(docs):
    tf()
    idf()
    cosine_sim()
    teleport()
    threshold()
    power()


if __name__ == "__main__":
    dataset_path = None
    filename = None

    while True:
        if not dataset_path:
            dataset_path = input(
                "name of the dataset folder please (It should be in this folder): "
            )
            if not os.path.exists(dataset_path):
                print("invalid path")
                dataset_path = None
                continue
        else:
            master = Summarizer(dataset_path)
            break

    while True:
        filename = input("file name please: ")
        filepath = os.path.join(dataset_path, filename)
        if not os.path.exists(filepath):
            print("no such file")
            continue
        else:
            master.summarize(filename)
            continue


def apply_teleportation_and_threshold(transition_matrix):
    similarity_threshold = 0.1

    transitions_above_threshold = np.zeros(transition_matrix.shape)

    N = len(transition_matrix)

    for i in range(N):
        columns = np.where(transition_matrix[i] > similarity_threshold)[0]
        transitions_above_threshold[i, columns] = 1 / len(columns)

    teleport_rate = 0.15

    uniform_matrix = np.full(transition_matrix.shape, 1 / N)

    teleport_probabilities = np.dot(teleport_rate, uniform_matrix)

    random_walk_probabilities = np.dot((1 - teleport_rate), transitions_above_threshold)

    markov_matrix = teleport_probabilities + random_walk_probabilities

    return markov_matrix