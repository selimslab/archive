"""

Network Simulation

Author: Selim Ozturk
Licence: MIT

Scenario: Users request movies over internet. 

sources: satellite and base station
user types: primary user(PU), secondary user(SU), hybrid user(HU) 

SU uses only base station, others can use both 
priorities on Satellite PU=HU
priorities on BaseStation PU>HU=SU


"""


class BaseStation(Satellite):
    total_power = 60
    number_of_bands = 10
    gain = 4 * (10 ** (-5))
    frequency = 700 * (10 ** 6)  # 700 mhz
    radius = 300  # meters
    distance = 150  # meters
    noise = 1.5 * (10 ** (-19))
    bandwidth = 2 * (10 ** 6)  # 2 mhz

    def __init__(self, env, name):
        super(BaseStation, self).__init__(env, name)
