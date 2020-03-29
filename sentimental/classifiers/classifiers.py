from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

from sklearn.discriminant_analysis import LinearDiscriminantAnalysis
from sklearn.naive_bayes import MultinomialNB
from sklearn import svm


def support_vector_machine(X_train, y_train, X_test, y_test):
    clf = svm.SVC()
    print("SVM classifier fitting...")
    clf.fit(X_train, y_train)

    print("SVM classifier predicting...")
    y_pred = clf.predict(X_test)

    print("SVM accuracy: ", accuracy_score(y_test, y_pred))


def multinomial_nb(all_tweets, vocabulary, labels):
    one_hots = []
    voc_size = len(vocabulary)
    for tweet in all_tweets:
        tweet_vector = [0] * voc_size
        for i, word in enumerate(tweet):
            if word in vocabulary:
                tweet_vector[i] = 1
            else:
                tweet_vector[i] = 0
        one_hots.append(tweet_vector)

    X_train, X_test, y_train, y_test = train_test_split(
        one_hots, labels, test_size=0.3, random_state=42
    )
    print("multinomial NB classifier fitting...")
    clf = MultinomialNB()
    clf.fit(X_train, y_train)

    print("MNB classifier predicting...")
    y_pred = clf.predict(X_test)

    print("MNB accuracy: ", accuracy_score(y_test, y_pred))


def linear_discriminant_analysis(X_train, y_train, X_test, y_test):
    clf = LinearDiscriminantAnalysis()

    print("Linear discriminant analysis fitting...")
    clf.fit(X_train, y_train)

    print("LDA classifier predicting...")
    y_pred = clf.predict(X_test)

    print("LDA accuracy: ", accuracy_score(y_test, y_pred))
