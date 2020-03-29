from pymongo import MongoClient
from west.AbstractSpiderFactory import BaseDB
from west.helpers.date_time import todays_date, date_of_x_days_ago


class EmlakDatabase(BaseDB):
    client = MongoClient(
        host="mongodb://<dbuser>:<dbpassword>@ds161653.mlab.com:61653/immobiliare",
        port=47450,
        username="selim",
        password="west123",
        authSource="immobiliare",
        authMechanism="SCRAM-SHA-1",
    )

    db_name = "immobiliare"
    db = client[db_name]
    collection_name = "emlak"

    def __init__(self):
        super(BaseDB, self).__init__()

    @classmethod
    def get_items_without_details(cls):
        return cls.db[cls.collection_name].distinct(
            "link", {"details": {"$exists": False}}
        )

    def save_updated_item(self, link, updates):
        # update only changed fields
        self.db[self.collection_name].find_one_and_update(
            {"link": link}, {"$set": updates, "$inc": {"days_on_market": 1}}  # filter
        )

    def save_details(self, link, details):
        self.db[self.collection_name].update_one(
            {"link": link}, {"$set": {"details": details}}
        )

    def check_removed_items(self):
        # set all as not updated first
        date_of_yesterday = date_of_x_days_ago(2)

        self.db[self.collection_name].update_many(
            {"dates.last_seen": {"$lt": date_of_yesterday}},
            {"$set": {"status.removed": True, "dates.removed": todays_date}},
        )


emlak_database = EmlakDatabase()

if __name__ == "__main__":
    pass
