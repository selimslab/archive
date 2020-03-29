from Classifier import NaiveBayesClassifier


if __name__ == "__main__":
    directory = "Dataset"
    topics = {"earn", "acq", "money-fx", "grain", "crude"}

    k = 50
    nb = NaiveBayesClassifier(directory, topics)

    alpha = 0.000001  # prevent log(0)
    nb.test_using_the_most_informative_words(alpha, k)
    nb.test_using_all_words(alpha)

    alpha = 1
    nb.test_using_the_most_informative_words(alpha, k)
    nb.test_using_all_words(alpha)
