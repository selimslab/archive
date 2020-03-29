# native
from os.path import exists
import pickle

# own packages
from word_embeddings.word2vec import word2vec
from word_embeddings.doc2vec import doc2vec
from preprocess import DataManager
from services.cleaner import Cleaner
from services.file_IO import write_linebyline, read_file, read_linebyline
from classifiers.keras_neural_net import train_neural_net
from word_embeddings.fasttext import fasttext
from services import file_IO

# vendor
import numpy as np
from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import pad_sequences
from keras.utils import to_categorical
from keras.models import load_model


def vectorize(tweets):
    tokenizer = Tokenizer(nb_words=10000)  # only the top 10000 words are considered.
    tokenizer.fit_on_texts(tweets)
    sequences = tokenizer.texts_to_sequences(tweets)  # in form of [7, 963, 5920, 5921]
    word_index = tokenizer.word_index
    print("Found %s unique tokens." % len(word_index))
    tweet_vectors = pad_sequences(
        sequences, maxlen=50
    )  # now every sentence is a list of 50 numbers
    return tweet_vectors, word_index


def train():
    # get clean data
    dm = DataManager()
    dm.read_training_data()
    tweets, labels = dm.get_clean_data()

    labels = to_categorical(labels, num_classes=3)  # make labels keras compatible
    vector_size = 300

    word2vec_model = word2vec(tweets, vector_size)
    doc2vec_model = doc2vec(tweets, vector_size)
    # tweet_vectors = np.array(doc2vec_model.docvecs.vectors_docs)

    tweet_vectors, word_index = vectorize(tweets)
    with open("word_index.inx", "wb") as file:
        pickle.dump(word_index, file)
    # train_tweets, test_tweets, train_labels, test_labels = train_test_split(tweets, labels, test_size = 0.1, random_state = 42)

    fasttext_model = fasttext(tweets, vector_size)  # train fast text

    wiki_index = eval(read_file("wiki_index.txt"))  # get word to line number dict

    embedding_matrix = get_embedding_matrix(word_index, wiki_index, fasttext_model)

    # train_tweet_vecs, test_tweet_vecs, train_labels, test_labels = train_test_split(tweet_vectors, labels, test_size = 0.1, random_state = 42)

    if exists("neural"):
        print("reading neural network model")
        model = load_model("neural")
    else:
        model = train_neural_net(
            tweet_vectors, labels, embedding_matrix, len(word_index), vector_size
        )
        model.save("neural")


def get_tweet_vector(word_index, tweet: list, max_vector_length):
    index_list = []
    for i, word in enumerate(tweet):
        if word in word_index:
            x = word_index[word]
            index_list.append(x)
        else:
            index_list.append(0)
    index_list = [0] * (max_vector_length - len(index_list)) + index_list
    index_list = np.asarray(index_list)
    return index_list


def predict(tweets, vector_size):

    if exists("neural"):
        print("reading neural network model")
        model = load_model("neural")
    else:
        print("Coulnd't find model")
        return

    if exists("word_index.inx"):
        with open("word_index.inx", "rb") as f:
            print("reading word_index")
            word_index = pickle.load(f)
    else:
        print("Coulnd't find word index file")
        return

    twl = []
    for tweet in tweets:
        tw = get_tweet_vector(word_index, tweet, vector_size)
        twl.append(tw)
    twl = np.asarray(twl)

    print("predicting labels...")
    output = model.predict(twl)
    output_labels = []
    labeldict = {0: 0, 1: 1, 2: -1}
    for row in output:
        result = np.argmax(row)
        result = labeldict[result]
        output_labels.append(result)
    return output_labels


def test(input_file):
    tweets = file_IO.read_linebyline(input_file)
    cleaner = Cleaner()
    tweets = cleaner.clean(tweets)
    vector_size = 300
    labels = predict(tweets, vector_size)
    write_linebyline("output.txt", labels)


def evaluate():
    out = read_linebyline("output.txt")
    true_outputs = read_linebyline("test/out")

    correct = 0
    false = 0
    for i in range(len(out)):
        if out[i] == true_outputs[i]:
            correct += 1
        else:
            false += 1

    print("success ", float(correct) / (correct + false))


if __name__ == "__main__":
    mode = input("Press 1 to train, 2 to test: ")
    if mode == "1":
        train()
    if mode == "2":
        test("test/test")
        evaluate()
    if mode == "3":
        evaluate()
