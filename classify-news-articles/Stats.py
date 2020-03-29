class Stats(object):
    @staticmethod
    def get_total_word_count(dataset):
        count = 0
        for tag in dataset:
            count += len(dataset[tag])
        return count

    @staticmethod
    def get_total_doc_count(dataset):
        count = 0
        for tag in dataset:
            count += dataset[tag]
        return count

    @staticmethod
    def get_count_of_a_word_in_all_classes(dataset, word):
        count = 0
        for tag in dataset:
            if word in dataset[tag]:
                count += dataset[tag][word]
        return count

    @staticmethod
    def get_megadoc_size(dataset, tag):
        count = 0
        for word in dataset[tag]:
            count += dataset[tag][word]
        return count
