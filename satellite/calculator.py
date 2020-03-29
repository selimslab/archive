import math
import matplotlib.pyplot as plt

# 100 distinct content


class Calculator(object):

    def __init__(self, arg):
        super(Calculator, self).__init__()
        self.arg = arg

    @staticmethod
    def calculate_power_strength(
        user_gain, total_power, number_of_bands, source_gain, f_source, source_distance
    ):
        c = 299792458
        pi = 3.14159265359
        per_channel_power = total_power / number_of_bands
        power_strength = (per_channel_power * source_gain * user_gain * (c ** 2)) / (
            4 * pi * f_source * source_distance
        ) ** 2
        return power_strength

    @staticmethod
    def calculate_channel_capacities(power_strength, bandwidth, noise):
        capacity = bandwidth * math.log(1 + (power_strength / (noise * bandwidth)), 2)
        return capacity

    @staticmethod
    def draw(random, algo):
        names = ["HU", "PU", "SU"]
        plt.subplot(121)
        plt.bar(names, random)
        plt.title("Random")
        plt.subplot(122)
        plt.bar(names, algo)
        plt.title("Algo")

        plt.suptitle("Resource Allocation")
        plt.show()
