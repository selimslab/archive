from pymongo import MongoClient, GEOSPHERE, ASCENDING
from scrapy.exceptions import DropItem
from pymongo.errors import DuplicateKeyError

# first duplicate check, only checks duplicates in the current job
class DuplicatesPipeline(object):
    def __init__(self):
        self.links_seen = set()

    def process_item(self, item, spider):
        if item["link"] in self.links_seen:
            raise DropItem("Duplicate item found: %s" % item)
        else:
            self.links_seen.add(item["link"])
            return item


class MongoPipeline(object):
    def __init__(self):
        pass

    def open_spider(self, spider):
        self.client = MongoClient(
            host="mongodb://<dbuser>:<dbpassword>@ds147450.mlab.com:47450/westeros",
            port=47450,
            username="west",
            password="west123",
            authSource="westeros",
            authMechanism="SCRAM-SHA-1",
        )

        self.db = self.client["westeros"]  # db name
        self.last_count = self.db.ads.count()  # ads is collection name

    def close_spider(self, spider):
        self.db.ads.create_index(
            [("link", ASCENDING)], unique=True
        )  # prevent duplicate ads next time
        self.db.ads.create_index([("coordinates", GEOSPHERE)])  # index coordinates
        self.client.close()

    def process_item(self, item, spider):
        try:
            self.last_count += 1
            item["id"] = self.last_count
            self.db.ads.insert_one(dict(item))
            return item
        except DuplicateKeyError:
            return "duplicate item"
