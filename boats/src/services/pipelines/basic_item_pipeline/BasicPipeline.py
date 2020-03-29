# -*- coding: utf-8 -*-


class BasicPipeline(object):
    def __init__(self):
        db_api.check_removed_items()
        self.links_seen = db_api.get_distinct_items_by_key("link")
        self.updates = list()
        self.inserts = list()

    def process_item(self, item, spider):

        link = item.get("link")

        if link in self.links_seen:
            updates = update(link, item)
            new_update_operation = db_api.update_and_increment_day(link, updates)
            self.updates.append(new_update_operation)
            if len(self.updates) >= 50:
                self.updates = db_api.bulk_exec(self.updates)
            return updates
        else:
            item = add_new(item)
            new_insert = db_api.InsertOne(item)
            self.inserts.append(new_insert)
            if len(self.inserts) >= 50:
                self.inserts = db_api.bulk_exec(self.inserts)
            return item

    def close_spider(self, spider):
        db_api.check_removed_items()
        db_api.bulk_exec(self.updates)
        db_api.bulk_exec(self.inserts)
