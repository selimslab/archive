# -*- coding: utf-8 -*-
import scrapy


class BaseSpider(scrapy.Spider):

    # entry point
    def __init__(self, *args, **kwargs):
        self.base_domain = "www.yachtworld.com"
        self.base_url = "https://" + self.base_domain
        # self.allowed_domains = [self.base_domain]
        self.start_urls = list()
        self.next_page = True

    def parse(self, response):
        pass
