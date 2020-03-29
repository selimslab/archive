from services.cleaner import Cleaner
from services.file_IO import read_linebyline


class DataManager:
    def __init__(self):
        self.positive_tweets = []
        self.negative_tweets = []
        self.neutral_tweets = []

        self.all_tweets = []
        self.labels = []

        self.vocabulary = {}

    def read_training_data(self):
        self.positive_tweets = self.get_tweet_texts("Train/positive-train")
        self.negative_tweets = self.get_tweet_texts("Train/negative-train")
        self.neutral_tweets = self.get_tweet_texts("Train/notr-train")

    def get_clean_data(self):
        VacuumCleaner = Cleaner()

        # turkish_positive = read_linebyline('helper_data/turkish_positive.txt')
        # turkish_negative = read_linebyline('helper_data/turkish_negative.txt')

        self.positive_tweets = VacuumCleaner.clean(self.positive_tweets)
        self.positive_vocabulary = VacuumCleaner.doc_frequencies

        self.negative_tweets = VacuumCleaner.clean(self.negative_tweets)
        self.negative_vocabulary = VacuumCleaner.doc_frequencies

        self.neutral_tweets = VacuumCleaner.clean(self.neutral_tweets)
        self.neutral_vocabulary = VacuumCleaner.doc_frequencies

        self.vocabulary = VacuumCleaner.vocabulary

        self.all_tweets = (
            self.positive_tweets + self.negative_tweets + self.neutral_tweets
        )
        self.labels = (
            [1] * len(self.positive_tweets)
            + [-1] * len(self.negative_tweets)
            + [0] * len(self.neutral_tweets)
        )

        return self.all_tweets, self.labels

    @staticmethod
    def get_tweet_texts(filepath):
        data = read_linebyline(filepath)
        tweets = []
        for line in data:
            line = line.split("\t\t\t")
            tweet = line[1]
            tweets.append(tweet)
        return tweets

    def summarize(self):
        print(len(self.all_tweets), "all tweets", self.all_tweets[0:1])
        print(len(self.positive_tweets), "positive tweets")
        print(len(self.negative_tweets), "negative tweets")
        print(len(self.neutral_tweets), "neutral tweets")

        print(len(self.vocabulary), "all vocabulary")
        print(len(self.positive_vocabulary), "positive vocabulary")
        print(len(self.negative_vocabulary), "negative vocabulary")
        print(len(self.neutral_vocabulary), "neutral vocabulary")

    def print_vocab(self):
        d = self.vocabulary
        for w in sorted(d, key=d.get, reverse=True):
            print(w, d[w])
