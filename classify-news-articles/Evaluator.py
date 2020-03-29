from DataManager import DatasetCreator


class Evaluator(object):
    def __init__(self, tags):
        self.tags = tags
        self.micro_average_table = {"tp": 0, "tn": 0, "fp": 0, "fn": 0}
        self.truth_tables = {}
        self.create_truth_tables()
        self.macro_averages = {"precision": 0, "recall": 0, "f-score": 0}
        self.scores = DatasetCreator.create_empty_dataset(tags)
        self.scores = {}

    def create_truth_tables(self):
        for tag in self.tags:
            self.truth_tables[tag] = {"tp": 0, "tn": 0, "fp": 0, "fn": 0}

    def update_truth_tables(self, guess, correct_class):
        if guess == correct_class:
            for tag in self.tags:
                if tag == guess:
                    self.truth_tables[tag]["tp"] += 1
                else:
                    self.truth_tables[tag]["tn"] += 1
        else:
            for tag in self.tags:
                if tag == correct_class:
                    self.truth_tables[correct_class]["fn"] += 1
                elif tag == guess:
                    self.truth_tables[guess]["fp"] += 1
                else:
                    self.truth_tables[tag]["tn"] += 1

    def update_micro_avg_table(self, tp, fp, fn, tn):
        self.micro_average_table["tp"] += tp
        self.micro_average_table["fp"] += fp
        self.micro_average_table["fn"] += fn
        self.micro_average_table["tn"] += tn

    # WRONG
    def calculate_scores(self, tag):
        tp = self.truth_tables[tag]["tp"]
        fp = self.truth_tables[tag]["fp"]
        fn = self.truth_tables[tag]["fn"]
        tn = self.truth_tables[tag]["tn"]

        self.update_micro_avg_table(tp, fp, fn, tn)

        precision = tp / float(tp + fp)
        recall = tp / float(tp + fn)
        f_score = 2 * precision * recall / float(precision + recall)

        self.macro_averages["precision"] += precision
        self.macro_averages["recall"] += recall
        self.macro_averages["f-score"] += f_score

        self.scores[tag] = [precision, recall, f_score]
        self.scores[tag] = ["%.2f" % score for score in self.scores[tag]]

    def calculate_averages(self):
        self.micro_averages = {}

        self.micro_averages["precision"] = float(self.micro_average_table["tp"]) / (
            self.micro_average_table["tp"] + self.micro_average_table["fp"]
        )
        self.macro_averages["precision"] /= len(self.tags)

        self.micro_averages["recall"] = float(self.micro_average_table["tp"]) / (
            self.micro_average_table["tp"] + self.micro_average_table["fp"]
        )
        self.macro_averages["recall"] /= len(self.tags)

        self.micro_averages["f-score"] = (
            2
            * self.micro_averages["precision"]
            * self.micro_averages["recall"]
            / float(self.micro_averages["precision"] + self.micro_averages["recall"])
        )
        self.macro_averages["f-score"] = (
            2
            * self.macro_averages["precision"]
            * self.macro_averages["recall"]
            / float(self.macro_averages["precision"] + self.macro_averages["recall"])
        )

        self.roundScores(self.micro_averages)
        self.roundScores(self.macro_averages)

        self.metric_names = ["precision", "recall", "f-score"]

    def roundScores(self, dict):
        for value in dict:
            dict[value] = round(dict[value], 2)

    def print_summary(self, alpha):
        print "alpha = %d" % int(alpha)

        row_format = "{:>16}" * (1 + 3)

        print row_format.format("", *self.metric_names)
        for tag in self.tags:
            print row_format.format(tag, *self.scores[tag])

        print row_format.format(
            "micro-avgs",
            *[
                self.micro_averages["precision"],
                self.micro_averages["recall"],
                self.micro_averages["f-score"],
            ]
        )
        print row_format.format(
            "macro-avgs",
            *[
                self.macro_averages["precision"],
                self.macro_averages["recall"],
                self.macro_averages["f-score"],
            ]
        )

        print "\n"
