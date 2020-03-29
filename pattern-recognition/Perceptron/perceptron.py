import numpy as np
from random import shuffle, choice

w1 = [
    [0.1, 1.1],
    [6.8, 7.1],
    [-3.5, -4.1],
    [2.0, 2.7],
    [4.1, 2.8],
    [3.1, 5.0],
    [-0.8, -1.3],
    [0.9, 1.2],
    [5.0, 6.4],
    [3.9, 4.0]
]

w2 = [
    [7.1, 4.2],
    [-1.4, -4.3],
    [4.5, 0],
    [6.3, 1.6],
    [4.2, 1.9],
    [1.4, -3.2],
    [2.4, -4.0],
    [2.5, -6.1],
    [8.4, 3.7],
    [4.1, -2.2]
]


def split_list(a_list):
    half = len(a_list) // 2
    return a_list[:half], a_list[half:]


def predict(sample, weights):
    prediction = np.dot(sample, weights)
    return bool(prediction)


def single_sample_perceptron(samples):
    dimensions = len(choice(samples)) - 1
    weights = np.zeros(dimensions + 1)
    # learning_rate = 1
    max_iterations = 100

    while True:
        iteration = 0
        correctly_classified = 0
        number_of_samples = len(samples)
        for sample in samples:
            prediction = predict(sample, weights)
            if prediction is not True:
                print('misclassified ', sample)
                print('old weights: ', weights)
                weights += sample #  [learning_rate * point for point in sample]
                print('new weights: ', weights)
            else:
                correctly_classified += 1
        if correctly_classified == number_of_samples:
            print('finished, weights: ', weights)
            break
        iteration += 1
        if iteration >= max_iterations:
            break


def ho_kashyap():
    pass


training1, test1 = split_list(w1)
training2, test2 = split_list(w2)

samples = list()
for point in training1:
    augmented = [1] + point
    samples.append(augmented)

for point in training2:
    augmented = [1] + point
    normalized = [-1 * item for item in augmented]
    samples.append(normalized)


single_sample_perceptron(samples)
