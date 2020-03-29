import re
from typing import List
from .PorterStemmer import PorterStemmer

# fuzzy
# lexrank
# stemmer
"""
pagerank 
tfidf 
cosine sim. 

index all wikipedia 
build a simple searchbox 
return article link, image, first 3 sentences, and summary 
"""


class Doc:
    text: str
    id: int


class Indexer:
    def __init__(self):
        pass


class SearchEngine:
    recent_searches = list()
    pass


class Summarizer:
    pass


def token_generator(text: str, stopwords: set):
    stemmer = PorterStemmer()
    for position, token in enumerate(text.lower().split()):
        token = re.sub("[^\w]", "", token)
        token = stemmer.stem(token, 0, len(token) - 1)
        if token not in stopwords:
            yield position, token


def build_index(docs: List[Doc]):
    inverted_index = dict()
    stopwords = set()
    default_node = {
        "positions": {},
        "frequency": 0
    }
    docs = []
    for doc in docs:
        for position, token in token_generator(doc, stopwords):
            node = inverted_index.get(token, default_node)
            node["positions"][doc.id] = node["positions"].get(doc.id, []) + [position]
            node["positions"][doc.id]["frequency"] += 1
            inverted_index[token] = node


def search():
    pass





if __name__ == "__main__":
    repl()
