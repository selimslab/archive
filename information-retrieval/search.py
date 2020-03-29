from PorterStemmer import PorterStemmer
import re





    def positional_intersect(self, pp1, pp2, k):
        answer = []
        i = j = 0
        while i != len(pp1):
            while j != len(pp2):
                if abs(pp2[j] - pp1[i]) <= k:
                    answer.append([pp1[i], pp2[j]])
                elif pp2[j] > pp1[i]:
                    break
                j += 1
            i += 1

        return answer

    def handle_positional_query(self, index, intersections, distances, search_terms):
        answer = {}  # {doc_id1:[ [], [],..], doc_id2: }

        for doc_id in intersections:
            for i, term in enumerate(search_terms):

                # end of pairs
                if i + 1 >= len(search_terms):
                    break

                # get a token pair
                first_token = search_terms[i]
                next_token = search_terms[i + 1]

                try:
                    # get positional indexes
                    pp1 = index[first_token][doc_id]
                    pp2 = index[next_token][doc_id]
                    k = distances[i]
                except IndexError as e:
                    return e.message

                if doc_id in answer:
                    # first postings list will be the last elements of the previous answer
                    pp1 = []
                    for list in answer[doc_id]:
                        pp1.append(list[-1])

                    intersect = self.positional_intersect(pp1, pp2, k)
                    # no further matches, abort
                    if len(intersect) == 0:
                        answer.pop(doc_id, None)
                        break

                    # merge intersect lists
                    for group in intersect:
                        result = []
                        # merge w1 /k1 w2 and w2 /k2 w3
                        for list in answer[doc_id]:
                            if list[-1] == group[0]:
                                list.append(group[1])
                                result.append(list)
                            answer[doc_id] = result

                else:
                    answer[doc_id] = self.positional_intersect(pp1, pp2, k)

                # remove empty results
                if len(answer[doc_id]) == 0:
                    answer.pop(doc_id, None)

        return answer

    # returns the index entries for query terms as a dict
    def get_index(self, terms):
        index = {}
        for term in terms:
            if term in self.dictionary:
                token_id = self.dictionary[term]
                index[term] = self.read_line(token_id)
        return index

    def get_distances(self, search_terms):
        distances = []
        digit_strings = re.findall(r"\/\d+", " ".join(search_terms))
        for k in digit_strings:
            k = re.sub("\/", "", k)
            k = int(k)
            distances.append(k)
        return distances

    def get_intersections(self, index):
        postings_lists = []
        for token in index:
            # doc_ids will be a list
            doc_ids = index[token].keys()
            postings_lists.append(doc_ids)

        # map applies set() function to every sublist
        setlist = map(set, postings_lists)

        # *args means arbitrary number of arguments
        common_set = set.intersection(*setlist)

        return list(common_set)

    def preprocess(self, query):
        p = PorterStemmer()
        result = []

        for word in query:
            # remove stopwords
            if word not in self.stopwords:
                # remove punctuation, keep /k
                if re.match(r"\/\d+", word) != None:
                    result.append(word)
                else:
                    word = re.sub("[^\w]", "", word)
                    stem = p.stem(word, 0, len(word) - 1)
                    result.append(stem)

        return result

    def search(self, query):
        answer = []
        if query is None:
            return answer

        query = query.lower().split(" ")
        try:
            query_type = int(query[0])
            if query_type not in (1, 2, 3):
                return "invalid input"
        except ValueError as e:
            print e.message
            return "invalid input"

        # stem, remove stopwords
        query = self.preprocess(query)

        search_terms = query[1:]

        # index of query terms only, in the form { token1 : {doc_id : [postings], ..}, token2 : ... }
        index = self.get_index(search_terms)

        if index:
            intersections = self.get_intersections(index)
        else:
            return answer

        if query_type == 1:
            answer = intersections

        elif query_type == 2:
            distances = [1] * (len(search_terms) - 1)
            answer = self.handle_positional_query(
                index, intersections, distances, search_terms
            )

        elif query_type == 3:
            distances = []
            # get /k terms
            for term in search_terms:
                if re.match(r"\/\d+", term):
                    k = re.sub("\/", "", term)
                    k = int(k)
                    distances.append(k)
                    # remove /k terms
                    search_terms.remove(term)

            answer = self.handle_positional_query(
                index, intersections, distances, search_terms
            )

        return answer


def go():
    whitefang = SearchEngine()
    while True:
        query = raw_input("Your query:\n")
        print whitefang.search(query)
        continue


if __name__ == "__main__":
    go()
