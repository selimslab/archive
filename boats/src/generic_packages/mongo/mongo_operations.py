from pymongo import UpdateOne
import pprint
from pymongo.errors import DuplicateKeyError
from src.generic_packages.time_util import date_of_x_days_ago


######################
# CREATE
######################
def save_new_item(collection, item):
    try:
        collection.insert_one(item)
    except DuplicateKeyError:
        print("duplicate item")


def add_a_new_field(collection, field_name, field_data):
    query = {field_name: {"$exists": False}}
    update = {"$set": {field_name: field_data}}
    collection.find(query, update)


######################
# READ
######################
def count(collection):
    return collection.find({}).count()


def sample(collection, n):
    pp = pprint.PrettyPrinter(indent=4)
    cursor = collection.aggregate([{"$sample": {"size": n}}])
    for doc in cursor:
        pp.pprint(doc)


def get_items_without_details(collection):
    # 'removed': {'$ne': True},
    projection = {"details": {"$ne": True}}
    return collection.distinct("link", projection)


def get_saved_item(collection, link):
    query = {"link": link}
    projection = {"price": 1, "sale_pending": 1, "last_seen": 1}
    return collection.find_one(query, projection)


def get_distinct_items_by_key(collection, key_string):
    return collection.distinct(key_string)


######################
# UPDATE
######################
def update_and_increment_day(link, updates):
    query = {"link": link}
    command = {"$inc": {"days": 1}}
    if updates:
        command["$set"] = updates
    # collection.find_one_and_update(query, command)
    return UpdateOne(query, command)


def update_item(collection, link, updates):
    query = {"link": link}
    command = {"$set": updates}
    collection.update_one(query, command)


def check_removed_items(collection):
    three_days_ago = date_of_x_days_ago(3)
    query = {"last_seen": {"$lt": three_days_ago}}
    command = {"$set": {"removed": True}}
    collection.update_many(query, command)


def rename_field(collection, old_name, new_name):
    query = {}
    command = {"$rename": {old_name: new_name}}
    collection.update_many(query, command)


######################
# DELETE
######################
def remove_field(collection, field_name):
    query = {}
    command = {"$unset": {field_name: ""}}
    collection.update_many(query, command)
