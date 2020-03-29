## lex
a python3 implementation of [LexRank: Graph-based Lexical Centrality as Salience in Text Summarization.](https://www.cs.cmu.edu/afs/cs/project/jair/pub/volume22/erkan04a-html/erkan04a.html)

+ LexRank or lexical PageRank algorithm is based on Google's PageRank
+ It measures the relevance of sentences in a document instead of measuring relevance of web pages

## Algorithm 
+ Create a graph, nodes are sentences, edges are tf-idf cosine similarities between sentences
+ Apply thresholding
+ Introduce random jumps to turn process into an irreducible and aperiodic Markov chain. 
  + Such a Markov process is  guaranteed to converge to a unique stationary distribution. 
+ Use power method to find the stationary distribution. 
  + Start with a uniform distribution. 
  + At each iteration, the eigenvector is updated by multiplying with the transpose of the stochastic matrix.

+ The result is a salience score for every sentence 


## requirements
+ numpy

`python3 lexrank.py`


## lexrank algorithm
1. get story and gold summary
2.  calculate lexrank scores
    * calculate tf
    * create cosine similarity graph
    * empty table with numpy.zeros
    * fill up for every sentence 
3. apply teleportation and threshold, make the matrix a markov matrix 
4. apply power method to the markov matrix

![](https://www.cs.cmu.edu/afs/cs/project/jair/pub/volume22/erkan04a-html/img23.gif)  

![](https://www.cs.cmu.edu/afs/cs/project/jair/pub/volume22/erkan04a-html/img24.gif)

[article](https://www.cs.cmu.edu/afs/cs/project/jair/pub/volume22/erkan04a-html/erkan04a.html)
