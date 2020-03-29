from pymongo import UpdateOne, InsertOne

from src.generic_packages.mongo.bulk_update import bulk_exec
from tqdm import tqdm

from src.generic_packages.file_IO.json_util import read_json
from src.temporary_storage.paths import items_path


def push_processed_items_to_mongo(collection):
    items = read_json(items_path)
    links = collection.distinct("link")
    inserts = list()
    updates = list()
    for item in tqdm(items):
        link = item.get("link")
        if link in links:
            command = {"$set": {}}
            updates.append(UpdateOne({"link": link}, command))
        else:
            inserts.append(InsertOne(item))
            if len(inserts) >= 500:
                inserts = bulk_exec(collection, inserts)

    inserts = bulk_exec(collection, inserts)
