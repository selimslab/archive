import numpy as np
from calculator import Calculator
from content import Content
import simpy
import threading
import time


class Satellite(object):
    frequency = 20 * (10 ** 9)  # 20 ghz
    gain = 2.5 * (10 ** 4)
    distance = 300 * (10 ** 3)  # 300 km to meters
    total_power = 240  # W
    number_of_bands = 5
    noise = 10 ** (-18)  # watt/hz
    bandwidth = 36 * (10 ** 6)  # 36 mhz

    def __init__(self, env, name):
        self.env = env
        self.name = name
        self.channels = simpy.PreemptiveResource(env, self.number_of_bands)

    @classmethod
    def calculate_service_duration(cls, user_gain, chunk_size):
        power_strength = Calculator.calculate_power_strength(
            user_gain,
            cls.total_power,
            cls.number_of_bands,
            cls.gain,
            cls.frequency,
            cls.distance,
        )
        capacity = Calculator.calculate_channel_capacities(
            power_strength, cls.bandwidth, cls.noise
        )
        return chunk_size / capacity
