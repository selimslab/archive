# -*- coding: utf-8 -*-


class DetailPipeline(object):
    def process_item(self, details, spider):
        link = details.pop("link")
        item_details = clean_details(details)
        item_details["details"] = True
        db_api.update_item(link, item_details)
        return item_details

    def close_spider(self, spider):
        db_api.check_removed_items()
