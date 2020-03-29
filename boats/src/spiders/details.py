from src.db import db_api
from scrapy import Request, Spider
from src.services.extract_data.get_detail_data import get_details


class DetailSpider(Spider):
    name = "details"

    custom_settings = {"ITEM_PIPELINES": {"src.helpers.pipelines.DetailPipeline": 200,}}

    full_spec_selector = "div.fullspecs div:first-child::text"

    def __init__(self, *args, **kwargs):
        super(DetailSpider, self).__init__(*args, **kwargs)
        self.start_urls = db_api.get_items_without_details()

    def start_requests(self):
        for link in self.start_urls:
            yield Request(url=link, meta={"link": link}, callback=self.parse)

    def parse(self, response):
        full_specs = response.css(self.full_spec_selector).extract()
        details = get_details(full_specs)
        details["link"] = response.meta.get("link")

        yield details
