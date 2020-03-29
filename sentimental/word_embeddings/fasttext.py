import os
from gensim.models import FastText
from test import test_fasttext


def fasttext(tweets, vector_size):
    if os.path.exists("fasttext"):
        print("reading fasttext model...")
        model = FastText.load("fasttext")
    else:
        print("training fasttext model...")
        model = FastText(
            tweets, size=vector_size, window=5, min_count=5, workers=4, sg=1
        )
        model.save("fasttext")
    test_fasttext(model)
    return model
