class User(object):
    """super class User"""

    count = 0
    got_basic = False
    got_HD = False
    delivered_base_chunks = 0
    delivered_enhancement_chunks = 0
    interrupted = 0

    def __init__(self, name):
        super(User, self).__init__()
        self.name = name

    @classmethod
    def show_throughput(cls):
        if cls.name == "HU":
            success_rate = (
                min(cls.delivered_base_chunks, cls.delivered_enhancement_chunks)
                / (cls.count * 1.0)
            ) * 100
            print "%d %s got %d base %d HD, success: %.2f %s" % (
                cls.count,
                cls.name,
                cls.delivered_base_chunks,
                cls.delivered_enhancement_chunks,
                success_rate,
                "%",
            )
            print (
                cls.delivered_base_chunks * Content.mean_base_chunk
                + cls.delivered_enhancement_chunks * Content.mean_enhancement_chunk
            ) / (simulation_time * (10 ** 6) * 1.0)
            return success_rate
        else:
            success_rate = ((cls.delivered_base_chunks) / (cls.count * 1.0)) * 100
            print "%d %s got %d base, success: %.2f %s" % (
                cls.count,
                cls.name,
                cls.delivered_base_chunks,
                success_rate,
                "%",
            )
            return success_rate

    @classmethod
    def choose_a_movie(cls):
        movies = Content.get_movies()
        random_movie_index = np.random.randint(0, 100)
        movie = movies.keys()[random_movie_index]
        movies[movie] += 1
        return movie

    @classmethod
    def simulate_arrival(cls, env, arrival_rate, random):
        new_users = np.random.poisson(arrival_rate)  # an integer 0, 1, 2 etc.
        for user in range(new_users):
            cls.count += 1
            new_user = cls("%s %d" % (cls.name, cls.count))
            if type(new_user).name == "HU":
                new_user.decide_base_source(env, random)
            else:
                new_user.decide_source(env, arrival_rate, random)

    # request a channel from source
    def request_content(
        self,
        env,
        user,
        is_enh_chunk,
        source,
        random,
        download_duration,
        priority_level,
        can_drop_others,
    ):

        movie = user.choose_a_movie()

        with source.channels.request(
            priority=priority_level, preempt=can_drop_others
        ) as channel:

            # A process cannot be interrupted if it already terminated.
            # A process can also not interrupt itself. Raise a RuntimeError in these cases.

            yield channel

            yield env.timeout(download_duration)

            if is_enh_chunk:
                user.got_HD = True
                type(user).delivered_enhancement_chunks += 1
            else:
                user.got_basic = True
                type(user).delivered_base_chunks += 1

            if type(user).name == "HU" and user.got_HD == False:
                user.decide_hd_source(env, random)
