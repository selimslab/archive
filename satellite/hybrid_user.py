class HybridUser(User):
    """docstring for HybridUser"""

    name = "HU"
    gain = 0.06
    arrival_rate = 3

    def __init__(self, name):
        super(HybridUser, self).__init__(name)

    sat_base_chunk_service_duration = Satellite.calculate_service_duration(
        gain, Content.mean_base_chunk
    )  # 15.4629023628
    bs_base_chunk_service_duration = BaseStation.calculate_service_duration(
        gain, Content.mean_base_chunk
    )  # 6.95132729053

    sat_enh_chunk_service_duration = Satellite.calculate_service_duration(
        gain, Content.mean_enhancement_chunk
    )  # 3.09258047257
    bs_enh_chunk_service_duration = BaseStation.calculate_service_duration(
        gain, Content.mean_enhancement_chunk
    )  # 1.39026545811

    def decide_base_source(self, env, random):
        base_request_from_sat = self.request_content(
            env,
            self,
            False,
            satellite,
            random,
            self.sat_base_chunk_service_duration,
            priority_level=1,
            can_drop_others=False,
        )
        base_request_from_bs = self.request_content(
            env,
            self,
            False,
            base_station,
            random,
            self.bs_base_chunk_service_duration,
            1,
            False,
        )
        if random:
            self.base_allocate_random(env, base_request_from_bs, base_request_from_sat)
        else:
            self.base_allocate_better(env, base_request_from_bs, base_request_from_sat)

    def decide_hd_source(self, env, random):
        hd_request_from_sat = self.request_content(
            env,
            self,
            True,
            satellite,
            random,
            self.sat_enh_chunk_service_duration,
            1,
            False,
        )
        hd_request_from_bs = self.request_content(
            env,
            self,
            True,
            base_station,
            random,
            self.bs_enh_chunk_service_duration,
            1,
            False,
        )
        if random:
            self.HD_allocate_random(env, hd_request_from_bs, hd_request_from_sat)
        else:
            self.HD_allocate_better(env, hd_request_from_bs, hd_request_from_sat)

    def base_allocate_better(self, env, base_request_from_bs, base_request_from_sat):
        # try base station first
        # if base_station.channels.count == BaseStation.number_of_bands:
        if satellite.channels.count == Satellite.number_of_bands:

            request = base_request_from_bs
        else:
            request = base_request_from_sat
        env.process(request)

    def HD_allocate_better(self, env, hd_request_from_bs, hd_request_from_sat):
        # try satellite first
        if satellite.channels.count == Satellite.number_of_bands:
            request = hd_request_from_bs
        else:
            request = hd_request_from_sat
        env.process(request)

    def base_allocate_random(self, env, base_request_from_bs, base_request_from_sat):
        if np.random.randint(1, 2) == 1:
            request = base_request_from_sat
        else:
            request = base_request_from_bs
        get_base = env.process(request)

    def HD_allocate_random(self, env, hd_request_from_bs, hd_request_from_sat):
        if np.random.randint(1, 2) == 1:
            request = hd_request_from_bs
        else:
            request = hd_request_from_sat
        # if self.got_basic:
        get_hd = env.process(request)
