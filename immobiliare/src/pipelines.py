# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://doc.scrapy.org/en/latest/topics/item-pipeline.html
from pymongo import MongoClient
from pymongo.errors import DuplicateKeyError, BulkWriteError

from algoliasearch import algoliasearch


class MongoPipeline(object):
    client = MongoClient(
        host="mongodb://<dbuser>:<dbpassword>@ds161653.mlab.com:61653/immobiliare",
        port=61653,
        username="selim",
        password="west123",
        authMechanism="SCRAM-SHA-1",
    )

    db_name = "immobiliare"
    collection_name = "links"
    db = client[db_name]


class AlgoliaPipeline(object):
    client = algoliasearch.Client("CCS0JY0UM1", "9f01cfca9e1b88b7f689a744c96707a6")
    index = client.init_index("immobiliare")


class ElasticSearchPipeline:
    def process_item(self, item, spider):
        pass


class ItemPipeline(MongoPipeline, AlgoliaPipeline):
    batch = list()

    def close_spider(self, spider):
        if self.batch:
            self.save()
        self.client.close()

    def save(self):
        try:
            self.db.ilanlar.insert_many(self.batch)
            self.index.add_objects(self.batch)  # push to algolia
            self.batch[:] = []
            return "ok"
        except DuplicateKeyError:
            return "duplicate item"
        except BulkWriteError:
            return "bulk write error"

    def process_item(self, item, spider):
        self.batch.append(dict(item))
        if len(self.batch) > 100:
            self.save()
        return item
