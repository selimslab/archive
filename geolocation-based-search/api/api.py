import os
import json

from eve import Eve

from scrapinghub import ScrapinghubClient
from scrapinghub import (
    ScrapinghubAPIError,
    Unauthorized,
    DuplicateJobError,
    BadRequest,
    NotFound,
    ValueTooLarge,
    ServerError,
)

from flask import request
from flask_cors import CORS

import schedule
import time

# Heroku support: bind to PORT if defined, otherwise default to 5000.
if "PORT" in os.environ:
    port = int(os.environ.get("PORT"))
    # use '0.0.0.0' to ensure your REST API is reachable from all your
    # network (and not only your computer).
    host = "0.0.0.0"
else:
    port = 7000
    host = "127.0.0.1"


app = Eve()  # create API
CORS(app)  # allow cross domain requests


@app.route("/close", methods=["POST"])
def close():
    results = []
    longitude = request.form["lon"]
    latitude = request.form["lat"]
    radius = request.form["radius"]

    if latitude and longitude and radius:
        coordinates = [float(longitude), float(latitude)]
        radius = float(radius) / 6371000  # rad to meters

        query = {"location": {"$geoWithin": {"$centerSphere": [coordinates, radius]}}}

        docs = app.data.driver.db["ads"].find(query)

        for doc in docs:
            results.append(
                {
                    "location": doc["location"],
                    "link": doc["link"],
                    "title": doc["title"],
                }
            )

        return json.dumps(results)
    else:
        return "please define your coordinates and radius"


@app.route("/crawl", methods=["GET", "POST"])
def get_urls():
    urls = request.args.get("urls")
    pagination = request.args.get("pagination").capitalize()  # false to False
    coordinates = request.args.get("coordinates").capitalize()

    return crawl(urls, pagination, coordinates)


def crawl(urls, pagination, coordinates):
    # connect to scrapy server
    scrapy_api_key = "5920ef3a57344e28ad926108571f47aa"
    scrapy_client = ScrapinghubClient(scrapy_api_key)
    # project parameters
    project_key = 317385
    spider_name = "emlak"
    # get spider
    project = scrapy_client.get_project(
        project_key
    )  # connect to the project on scrapy cloud
    spider = project.spiders.get(spider_name)  # select spider
    # run spider
    try:
        # urls are the pages to crawl,
        # pagination is to follow next pages or not
        # coordinates is to get coordinates or not
        job = spider.jobs.run(urls=urls, pagination=pagination, coordinates=coordinates)
        logs = []
        items = []
        for log in job.logs.iter():
            logs.push(log)
        for item in job.items.iter():
            items.push(item)

        app.logger.info("%s" % logs)
        app.logger.info("%s" % items)

        return json.dumps(logs)  # list to str
    except (
        ScrapinghubAPIError,
        DuplicateJobError,
        BadRequest,
        Unauthorized,
        NotFound,
        ValueTooLarge,
        ServerError,
    ) as exception:
        return type(exception).__name__


# SCHEDULING
# https://schedule.readthedocs.io/
scheduled_urls = ["", "", ""]
schedule.every().hour.do(crawl, scheduled_urls, pagination=False, coordinates=False)

"""
schedule.every().day.at("10:30").do(crawl, scheduled_urls, pagination=False)
schedule.every(10).minutes.do(job)
schedule.every().hour.do(job)
schedule.every().day.at("10:30").do(job)
schedule.every().monday.do(job)
schedule.every().wednesday.at("13:15").do(job)
"""


if __name__ == "__main__":
    app.run(host=host, port=port)
    while True:
        schedule.run_pending()
        time.sleep(1200)  # sleep 20 min
