"""
Author: Selim Ozturk
Created: Sep 22, 2019
Licence: MIT
"""
import os
import csv
from typing import Iterator
from datetime import datetime
import requests
import itertools


def row_generator(csv_path: str) -> Iterator[dict]:
    """
    Read a csv file row by row
    :param csv_path:
    :return: dicts of csv rows
    """
    try:
        with open(csv_path, "r") as csv_file:
            reader = csv.DictReader(csv_file)
            for row in reader:
                yield row
    except (FileNotFoundError,IOError) as e:
        print(e)



def str_to_date(date_string: str) -> datetime.date:
    return datetime.strptime(date_string, "%Y-%m-%d").date()


def post_csv_rows_the_api(api_endpoint: str, data_generator: Iterator[dict]):
    for row in data_generator:
        date_string = row.get("date")
        date = str_to_date(date_string)
        row["date"] = date
        r = requests.post(api_endpoint, data=row)
        print(r.status_code, r.reason)


def test_post_csv_rows_the_api():
    """ Create 3 example rows on DB """
    test_generator = itertools.islice(ROW_GENERATOR, 3)
    post_csv_rows_the_api(API_ENDPOINT, test_generator)



API_ENDPOINT = "http://127.0.0.1:8000/api/v1/records/"
DATA_PATH = "dataset.csv"
ROW_GENERATOR = row_generator(DATA_PATH)
# test_post_csv_rows_the_api()
post_csv_rows_the_api(API_ENDPOINT, ROW_GENERATOR)
