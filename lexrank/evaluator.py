from rouge import Rouge
from file_manager import FileManager


class Evaluator(object):
    def __init__(self):
        self.rouge_scores = {}

    def get_avg_rouges(self):
        avg_rouges = {
            "rouge-1": {"f": 0.0, "p": 0.0, "r": 0.0},
            "rouge-2": {"f": 0.0, "p": 0.0, "r": 0.0},
            "rouge-l": {"f": 0.0, "p": 0.0, "r": 0.0},
        }

        for filename in self.rouge_scores:
            for rouge in self.rouge_scores[filename]:
                for score in self.rouge_scores[filename][rouge]:
                    avg_rouges[rouge][score] += self.rouge_scores[filename][rouge][
                        score
                    ]

        length = len(self.rouge_scores)
        for rouge_type, scores in avg_rouges.items():
            for score in scores:
                avg_rouges[rouge_type][score] /= length
                avg_rouges[rouge_type][score] = round(avg_rouges[rouge_type][score], 3)

        FileManager.write_file("avg_rouges.txt", str(avg_rouges))

    def calculate_rouge(self, summary, gold_summary, filename):
        rouge = Rouge()
        scores = rouge.get_scores(summary, gold_summary, avg=True)
        self.rouge_scores[filename] = scores
