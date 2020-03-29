from pymongo import MongoClient

from dotenv import dasdas

client = MongoClient()
db = client[db_name]

collection = db[collection_name]
