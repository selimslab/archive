
### Intro
+ a document retrieval system for simple boolean queries.
+ Search in 21578 Reuters articles
+ The positional inverted indexing scheme is used

### Preprocessing
 + Lowercase
 + remove punctuations
 + split into lists
 + remove stopwords 
 + stem words with Porter Stemmer
 
### Indexing
+ There are pver 2 million tokens and over 70 000 unique tokens after preprocessing
+ A dictionary and positional index are created from the unique tokens and their positions 

### Querying 
+ When a user query a term, 
  + first preprocess the query
  + get positional indexes for all search terms 
  + return intersection 
  + also consider distances between terms for positional queries
  

## How to Run
+ python 2.7

`python main.py` to create dictionary and positional index

`python search.py` to search

 
## Source Code
> main.py traverses the files in 'Dataset' directory and creates 2 files:
1. dictionary.txt 
2. positional_index.txt

> search.py is the simple search engine

3 types of queries are supported
+ 1 oil AND price 
+ 2 oil price
+ 3 oil /3 price
