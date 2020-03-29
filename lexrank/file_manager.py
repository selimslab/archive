class FileManager(object):
    @staticmethod
    def read_file(filepath):
        content = None
        try:
            with open(filepath, "r") as f:
                print("reading file %s" % filepath)
                content = f.read()
        except IOError as e:
            print(e)
        return content

    @staticmethod
    def read_lines(filepath):
        content = None
        try:
            with open(filepath, "r") as f:
                print("reading file %s" % filepath)
                content = f.readlines()
        except IOError as e:
            print(e)
        return content

    @staticmethod
    def write_file(filepath, data):
        try:
            #  print 'writing to %s' % filepath
            with open(filepath, "w") as f:
                print("writing to %s" % filepath)
                f.write(data)
        except IOError as e:
            print(e)

    @classmethod
    def read_data_structure(cls, filepath):
        return eval(cls.read_file(filepath))
