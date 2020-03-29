from keras.layers import Dense, Conv1D, MaxPooling1D, LSTM, Dropout
from keras.models import Sequential
from keras.layers import Embedding


def train_neural_net(
    train_tweet_vecs, train_labels, embedding_matrix, number_of_words, vector_size
):

    print("training neural network model")
    print("embedding..")
    embedding_layer = Embedding(
        number_of_words,
        vector_size,
        weights=[embedding_matrix],
        # input_length=MAX_SEQUENCE_LENGTH,
        trainable=True,
    )

    lstm_out = 196
    model = Sequential()
    model.add(embedding_layer)
    model.add(Conv1D(128, 5, activation="relu"))
    model.add(MaxPooling1D(pool_size=2))
    model.add(Dropout(0.4))
    model.add(LSTM(lstm_out, dropout=0.2, recurrent_dropout=0.2))
    model.add(Dropout(0.4))
    model.add(Dense(3, activation="softmax"))
    model.compile(
        loss="categorical_crossentropy", optimizer="adam", metrics=["accuracy"]
    )

    print("training the model..")

    model.fit(train_tweet_vecs, train_labels, epochs=5, batch_size=128)
    return model
