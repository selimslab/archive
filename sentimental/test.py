def test_word2vec(model):
    tests = {"bogazici", "universite", "ogrenci", "basari", "terorist", "kadin"}
    for word in tests:
        print(word, model.most_similar(word))


def test_fasttext(model):
    tests = {"bogazici", "universite", "ogrenci", "basari", "terorist", "kadin"}
    for word in tests:
        print(word, model.wv.most_similar(word))


def test_doc2vec(model):
    tests = {"bogazici", "universite", "ogrenci", "basari", "terorist", "kadin"}
    for word in tests:
        print(word, model.most_similar(word))
