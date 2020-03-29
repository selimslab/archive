class FileManager(object):
    @staticmethod
    def read_file(filename):
        content = None
        try:
            with open(filename, "r") as f:
                print "reading file %s" % filename
                content = f.read()
        except IOError as e:
            print e.message
        return content

    @staticmethod
    def write_file(filename, data):
        try:
            #  print 'writing to %s' % filename
            with open(filename, "w") as f:
                f.write(data)
        except IOError as e:
            print e.message

    @classmethod
    def read_data_structure(cls, filename):
        return eval(cls.read_file(filename))
