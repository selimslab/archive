class Simulation(object):
    """docstring for Simulation"""

    def __init__(self, random):
        self.env = simpy.Environment()
        self.random = random

    def setup(self):
        sat = Satellite(self.env, "sat")
        bs = BaseStation(self.env, "bs")
        return sat, bs

    @staticmethod
    def get_throughputs():
        return (
            HybridUser.show_throughput(),
            PrimaryUser.show_throughput(),
            SecondaryUser.show_throughput(),
        )

    # new user arrives if poisson return true at that second
    def simulate_user_arrivals(self, simulation_time):
        for time in range(simulation_time):
            yield self.env.timeout(1)  # pass 1 second
            HybridUser.simulate_arrival(self.env, HybridUser.arrival_rate, self.random)
            PrimaryUser.simulate_arrival(
                self.env, PrimaryUser.arrival_rate_to_bs, self.random
            )
            SecondaryUser.simulate_arrival(
                self.env, SecondaryUser.arrival_rate, self.random
            )
            PrimaryUser.simulate_arrival(
                self.env, PrimaryUser.arrival_rate_to_sat, self.random
            )

    def run_simulation(self, simulation_time):
        if self.random:
            print "\n\t Random Allocation"
        else:
            print "\n\t Algorithmic Allocation"

        self.env.process(self.simulate_user_arrivals(simulation_time))
        self.env.run(until=simulation_time)
        most_wanted = Content.get_most_watched()
        print "most wanted:", most_wanted


simulation_time = 100


matrix = Simulation(random=True)
satellite, base_station = matrix.setup()
matrix.run_simulation(simulation_time)
random_success = Simulation.get_throughputs()


neo = Simulation(random=False)
satellite, base_station = neo.setup()
neo.run_simulation(simulation_time)
algorithm_success = Simulation.get_throughputs()


# Calculator.draw(random_success, algorithm_success)
