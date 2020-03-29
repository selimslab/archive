from file_IO import read_linebyline
import re
from nltk.tokenize import TweetTokenizer


def remove_garbage(word):
    word = re.sub("[^\w]", "", word)  # remove non-alphanumeric characters
    word = re.sub("\d+", "", word)  # remove digits
    if re.search("pictwittercom", word):
        word = ""
    return word


def clean_stopwords():
    with open("helper_data/stopwords_with_special_chars.txt", "r") as f:
        content = f.readlines()

    with open("helper_data/stopwords.txt", "w") as f:
        for word in content:
            word = replace_turkish_chars(word)
            f.write(str(word))


def replace_turkish_chars(word):
    word = re.sub("ş", "s", word)
    word = re.sub("ç", "c", word)
    word = re.sub("ü", "u", word)
    word = re.sub("ğ", "g", word)
    word = re.sub("ö", "o", word)
    word = re.sub("ı", "i", word)
    word = re.sub("ü", "u", word)
    return word


class Cleaner(object):
    def __init__(self):
        stopwords = read_linebyline("helper_data/stopwords_with_special_chars.txt")
        self.stopwords = set(stopwords)
        self.vocabulary = {}
        self.doc_frequencies = {}

        stems = read_linebyline("helper_data/stems.txt")

        self.stems = set(stems)

        new_stems = []
        for stem in list(self.stems):
            new_stems.append(replace_turkish_chars(stem))
        self.stems = set(new_stems)

    def clean(self, tweets):
        print("cleaning...")
        self.doc_frequencies = {}
        tokenizer = TweetTokenizer(strip_handles=True)
        for i, tweet in enumerate(tweets):
            tweet = tokenizer.tokenize(tweet)  # remove @username, split, etc.
            tweet = [self.normalize(word) for word in tweet]
            tweet = list(filter(None, tweet))  # remove empty strings
            tweets[i] = tweet

        return tweets

    def stem(self, word):
        for item in self.stems:
            if re.search(item, word):
                word = item
        return word

    def normalize(self, word):  # SHOULD KEEP SMILEYS??
        word = word.lower()
        word = remove_garbage(word)

        word = replace_turkish_chars(word)

        word = self.stem(word)

        if word in self.stopwords:
            word = ""

        return word
