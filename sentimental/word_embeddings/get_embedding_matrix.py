def get_embedding_matrix(word_index, wiki_index, fasttext_model):
    embedding_matrix = np.zeros((len(word_index), 300))
    print("creating embedding")
    counter = 0
    try:
        with open("wiki.tr.vec", "r") as f:
            for word, i in word_index.items():
                embedding_vector = None
                if word in wiki_index:
                    line_number = wiki_index[word]
                    for j, line in enumerate(f):
                        if j == line_number:
                            line = line.split(" ")
                            embedding_vector = line[1:-1]

                elif word in fasttext_model:
                    embedding_vector = fasttext_model[word]

                if embedding_vector is not None:
                    # words not found in embedding index will be all-zeros.
                    embedding_matrix[i] = embedding_vector
                    counter += 1
    except IOError as e:
        print(e)

    print(
        "Found vector of %d out of %d words in embedding" % (counter, len(word_index))
    )

    return embedding_matrix
