from west.AbstractSpiderFactory.BaseClasses import AbstractURLManager
from helpers.file_input_output import read_line_by_line
import re


class EmlakURLGenerator(AbstractURLManager):
    root_search_url = "sahibinden.com"
    query_parameters = ""

    def url_generator(self):
        pass


def generate_queries():
    mahalles = read_line_by_line("mahalle.txt")
    print(mahalles)
    for line in mahalles:
        if "data-label" in line:
            mahalle = re.findall("data-label='.+?'", line)[0]
            semt = re.findall("data-parentlabel='.+?'", line)[0]

            mahalle = mahalle.replace("data-label=", "")
            mahalle = mahalle.replace("'", "")

            semt = semt.replace("data-parentlabel=", "")
            semt = semt.replace("'", "")

            print(semt, mahalle)


def clean_labels():
    pass


generate_queries()
