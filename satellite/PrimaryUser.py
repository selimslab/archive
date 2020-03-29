class PrimaryUser(User):
    """docstring for PrimaryUser"""

    name = "PU"

    arrival_rate_to_sat = 0.15
    arrival_rate_to_bs = 0.8  # user/sec

    gain_using_satellite = 0.06
    gain_using_bs = 0.11

    def __init__(self, name):
        super(PrimaryUser, self).__init__(name)

    sat_service_duration = Satellite.calculate_service_duration(
        gain_using_satellite, Content.mean_base_chunk
    )  # 15.4629023628
    bs_service_duration = BaseStation.calculate_service_duration(
        gain_using_bs, Content.mean_base_chunk
    )  # 5.05945719479

    def decide_source(self, env, arrival_rate, random):
        # try satellite first
        if arrival_rate == self.arrival_rate_to_sat:
            # satellite.channels.count <= Satellite.number_of_bands:
            request = self.request_content(
                env, self, False, satellite, random, self.sat_service_duration, 1, False
            )
        else:
            request = self.request_content(
                env,
                self,
                False,
                base_station,
                random,
                self.bs_service_duration,
                1,
                False,
            )
        env.process(request)
