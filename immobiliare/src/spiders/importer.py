# -*- coding: utf-8 -*-
import scrapy
from src.helpers.dataextractor import DataExtractor
from src.helpers.url import URLManager


class PropertySpider(scrapy.Spider):
    name = "pro"
    base_domain = "sahibinden.com"
    base_url = "https://www.sahibinden.com"
    allowed_domains = [base_domain]

    # entry point
    def __init__(self, url, *args, **kwargs):
        super(PropertySpider, self).__init__(*args, **kwargs)
        url = URLManager.check_url(url)
        self.start_urls = [url]
        self.subdomain = url.replace("https://", "").split(".")[0]
        self.url = url

    def parse(self, response):
        is_detail_page = self.subdomain == "www"
        if is_detail_page:
            item = self.parse_item_detail(response)
            item["link"] = self.url
            yield item
        else:
            table = response.css("div.classified-list  table tbody")
            ilanlar = table.xpath("//tr")

            for ilan in ilanlar:
                link = ilan.css("td:nth-child(2) a::attr(href)").extract_first()
                if link:
                    yield scrapy.Request(
                        link, callback=self.parse_item_detail, meta={"link": link}
                    )

            next_page_selector = "ul.pageNaviButtons > li:last-child > a::attr(href)"
            next_page = response.css(next_page_selector).extract_first()
            if next_page:
                yield response.follow(next_page, callback=self.parse)

    def parse_item_detail(self, response):
        item = DataExtractor.extract_detail_page(response)
        if "link" in response.meta:
            item["link"] = response.meta["link"]
        return item
