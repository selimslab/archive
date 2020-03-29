# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# https://doc.scrapy.org/en/latest/topics/items.html

from scrapy import Item, Field


class EmlakItem(Item):
    # define the fields for your item here like:
    # name = scrapy.Field()

    # defining fields here enables you access them later using item['fieldName']
    title = Field()
    link = Field()
    location = Field()
    date = Field()
    year = Field()
    price = Field()
