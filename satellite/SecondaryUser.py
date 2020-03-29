class SecondaryUser(User):
    """docstring for SecondaryUser"""

    name = "SU"
    arrival_rate = 5
    gain = 0.06
    service_duration = BaseStation.calculate_service_duration(
        gain, Content.mean_base_chunk
    )  # 6.95132729053

    def __init__(self, name):
        super(SecondaryUser, self).__init__(name)

    def decide_source(self, env, arrival_rate, random):
        # queue for bs
        request = self.request_content(
            env,
            self,
            False,
            base_station,
            random,
            SecondaryUser.service_duration,
            2,
            False,
        )
        env.process(request)
