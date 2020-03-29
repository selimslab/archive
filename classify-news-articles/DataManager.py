import os
from FileManager import FileManager
from StoryProcessor import StoryProcessor


class DataManager(object):
    def __init__(self, topics):
        self.topics = topics
        self.training_vocabulary = set()
        self.test_vocabulary = set()

        self.doc_count_training = DatasetCreator.create_count_dict(topics)
        self.doc_count_test = DatasetCreator.create_count_dict(topics)

        self.test_set = DatasetCreator.create_empty_dataset(topics)
        self.training_set = DatasetCreator.create_empty_dataset(topics)

        self.docs_in_training_set = 0
        self.docs_in_test_set = 0

    def create_datasets(self, dataset):
        sonic = StoryProcessor(self.topics)
        for file in os.listdir(dataset):
            if file.endswith(".sgm"):
                filename = os.path.join(dataset, file)
                filecontent = FileManager.read_file(filename)
                for story in sonic.story_generator(filecontent):
                    if story:
                        self.update_records(story)

        self.save_data()

    def update_records(self, story):
        tag, dataset_type, doc_id, tokens = story
        if dataset_type == 1:  # training set
            self.docs_in_training_set += 1
            self.doc_count_training[tag] += 1
            self.update_dataset(self.training_set, tag, tokens)
            self.update_vocabulary(self.training_vocabulary, tokens)
        elif dataset_type == 2:  # test set
            self.docs_in_test_set += 1
            self.doc_count_test[tag] += 1
            self.update_test_set(tag, tokens, doc_id)
            self.update_vocabulary(self.test_vocabulary, tokens)
        else:
            pass

    @staticmethod
    def update_vocabulary(v, tokens):
        for token in tokens:
            if token not in v:
                v.add(token)

    @staticmethod
    def update_dataset(dataset, tag, tokens):
        for token in tokens:
            if token in dataset[tag]:
                dataset[tag][token] += 1
            else:
                dataset[tag][token] = 1

    def update_test_set(self, tag, tokens, doc_id):
        self.test_set[tag][doc_id] = tokens

    def save_data(self):
        FileManager.write_file("training.txt", str(self.training_set))
        FileManager.write_file("test.txt", str(self.test_set))

        FileManager.write_file("doc_count_training.txt", str(self.doc_count_training))
        FileManager.write_file("doc_count_test.txt", str(self.doc_count_test))

        FileManager.write_file("test_vocabulary.txt", str(self.test_vocabulary))
        FileManager.write_file("training_vocabulary.txt", str(self.training_vocabulary))

        print "docs_in_test_set", self.docs_in_test_set
        print "docs_in_training_set", self.docs_in_training_set
        print "test vocabulary length", len(self.test_vocabulary)
        print "training vocabulary length", len(self.training_vocabulary)


class DatasetCreator(object):
    @staticmethod
    def create_empty_dataset(topics):
        dataset = {}
        for topic in topics:
            dataset[topic] = {}
        return dataset

    @staticmethod
    def create_count_dict(topics):
        dataset = {}
        for topic in topics:
            dataset[topic] = 0
        return dataset
