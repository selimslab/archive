def read_file(filepath):
    content = None
    try:
        with open(filepath, "r") as f:
            print("reading file ", filepath)
            content = f.read()
    except IOError as e:
        print(e)
    return content


def read_linebyline(filepath):
    content = None
    try:
        with open(filepath, "r") as f:
            print("reading file line by line ", filepath)
            content = f.read().splitlines()
    except IOError as e:
        print(e)
    return content


def write_linebyline(filename, data):
    try:
        with open(filename, "w") as f:
            print("writing line by line ", filename)
            for line in data:
                f.write(str(line) + "\n")
    except IOError as e:
        print(e.message)


def write_file(filename, data):
    try:
        with open(filename, "w") as f:
            print("writing to ", filename)
            f.write(data)
    except IOError as e:
        print(e.message)
