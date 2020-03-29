import keras
from keras.models import Sequential
from keras.layers import Dense, Dropout


class neural:
    @staticmethod
    def train_neural_net(tweet_vectors, labels, vector_size):
        # make keras compatible
        labels = keras.utils.to_categorical(labels, num_classes=3)

        model = Sequential()
        model.add(Dense(1024, input_shape=(vector_size,), activation="sigmoid"))
        model.add(Dropout(0.5))
        model.add(Dense(512, input_shape=(vector_size,), activation="relu"))
        model.add(Dropout(0.5))
        model.add(Dense(256, activation="sigmoid"))
        model.add(Dropout(0.5))
        model.add(Dense(128, input_shape=(vector_size,), activation="relu"))
        model.add(Dropout(0.5))
        model.add(Dense(3, activation="softmax"))

        print("compiling...")
        model.compile(
            loss="categorical_crossentropy", optimizer="adam", metrics=["accuracy"]
        )

        print("training...")
        model.fit(
            tweet_vectors,
            labels,
            batch_size=128,
            epochs=5,
            verbose=1,
            validation_split=0.1,
            shuffle=True,
        )
