from gensim.models import Word2Vec
from gensim.models import Phrases
from gensim.models.phrases import Phraser

from test import test_word2vec
import os


def word2vec(all_tweets, vector_size):
    if os.path.exists("word2vec"):
        print("reading word2vec model")
        model = Word2Vec.load("word2vec")
    else:
        print("training word2vec model")
        phrases = Phrases(all_tweets)
        bigrams = Phraser(phrases)
        sentences = bigrams[all_tweets]
        model = Word2Vec(
            sentences=sentences,  # tokenized sentences, list of list of strings
            size=vector_size,  # size of embedding vectors
            workers=10,  # how many threads?
            min_count=5,  # minimum frequency per token, filtering rare words
            sample=0.05,  # weight of downsampling common words
            sg=1,  # should we use skip-gram? if 0, then cbow
            iter=5,
            hs=0,
        )
        model.train(sentences, total_examples=len(sentences), epochs=10)
        model.save("word2vec")

    test_word2vec(model)
    return model
