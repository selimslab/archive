from services.file_IO import write_file


def index():
    index = {}
    try:
        with open("wiki.tr.vec", "r") as file:
            for i, line in enumerate(file):
                l = line.split()
                if len(l) == 301:
                    word = l[0]
                    index[word] = i

    except IOError as e:
        print(e)

    write_file("wiki_index.txt", str(index))


index()
