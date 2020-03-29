from PorterStemmer import PorterStemmer
import re


class VacuumCleaner(object):
    @staticmethod
    def get_stopwords(filename):
        with open(filename, "r") as f:
            stopwords = f.read().split("\n")
        return set(stopwords)

    @staticmethod
    def stem(word):
        p = PorterStemmer()
        return p.stem(word, 0, len(word) - 1)

    @classmethod
    def clean(cls, data):
        tokens = []
        data = data.lower().split()
        stopwords = cls.get_stopwords("stopwords.txt")

        for word in data:
            # remove non-alphanumeric characters
            word = re.sub("[^\w]", "", word)
            # stem
            token = cls.stem(word)
            # remove stopwords
            if token not in stopwords:
                tokens.append(token)

        return tokens
