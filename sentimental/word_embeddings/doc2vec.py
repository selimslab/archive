from gensim.models import Doc2Vec
from gensim.models.doc2vec import TaggedDocument
import os
from test import test_doc2vec


def doc2vec(all_tweets, vector_size):
    if os.path.exists("doc2vec"):
        print("reading doc2vec model")
        model = Doc2Vec.load("doc2vec")

    else:
        print("training doc2vec model")
        docs = []
        for i, tweet in enumerate(all_tweets):
            docs.append(TaggedDocument(tweet, [i]))

        model = Doc2Vec(docs, iter=10, vector_size=vector_size, window=10, workers=10)
        model.save("doc2vec")
        test_doc2vec(model)

    return model
