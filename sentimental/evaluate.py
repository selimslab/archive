from services.cleaner import Cleaner
from keras.models import load_model
from services.file_IO import read_linebyline, write_linebyline
import pickle
import numpy as np
from keras.utils import to_categorical


def get_tweet_vector(word_index, tweet, max_vector_length):
    index_list = []
    for i, word in enumerate(tweet):
        if word in word_index:
            x = word_index[word]
            index_list.append(x)
    index_list = [0] * (max_vector_length - len(index_list)) + index_list
    index_list = np.asarray(index_list)
    return index_list


def test():

    tweets = read_linebyline("test/test")
    cleaner = Cleaner()
    tweets = cleaner.clean(tweets)

    model = load_model("neural")

    word_index = 0
    with open("word_index.inx", "rb") as f:
        word_index = pickle.load(f)

    vector_size = 50
    twl = []
    for tweet in tweets:
        tw = get_tweet_vector(word_index, tweet, vector_size)
        twl.append(tw)
    twl = np.asarray(twl)

    label_probabilities = model.predict(twl, vector_size)

    labeldict = {0: 1, 1: 0, 2: -1}
    predicted_labels = []
    for label in label_probabilities:
        result = np.argmax(label)
        result = labeldict[result]
        predicted_labels.append(result)

    write_linebyline("output.txt", str(predicted_labels))


def evaluate():
    true_labels = read_linebyline("test/out")
    true_labels = [int(label) for label in true_labels]
    true_labels = to_categorical(true_labels, 3)

    predicted_labels = read_linebyline("output.txt")
    predicted_labels = [int(label) for label in predicted_labels]

    correct = 0
    false = 0
    for i in range(len(true_labels)):
        if predicted_labels[i] == true_labels[i]:
            correct += 1
        else:
            false += 1

    print("success ", float(correct) / (correct + false))


if __name__ == "__main__":
    test()
    evaluate()
