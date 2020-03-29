from src.spiders.base import BaseSpider
from src.services.extract_data.get_item import get_item


class BasicSpider(BaseSpider):
    name = "basics"
    custom_settings = {"ITEM_PIPELINES": {"src.helpers.pipelines.BasicPipeline": 200,}}

    def __init__(self, *args, **kwargs):
        super(BasicSpider, self).__init__(*args, **kwargs)
        self.next_page = True
        self.next_page_button_selector = "span.navNext > a.navNext::attr(href)"
        self.search_results_table_selector = (
            '// *[ @ id = "searchResultsDetailsABTest"]/div'
        )
        self.start_urls = [
            "https://www.yachtworld.com/core/listing/cache/searchResults.jsp?fromLength=25&toLength=&fromYear=1995&toYear=&fromPrice=20000&toPrice=8000000&luom=126&currencyid=100&ps=50"
        ]

    def parse(self, response):
        search_results_table = response.xpath(self.search_results_table_selector)
        for row in search_results_table:
            item = get_item(row)

            link = item.get("link")
            if not link:
                continue

            if "http" not in link:
                item["link"] = self.base_url + link

            yield item

        # follow to the next page
        if self.next_page:
            next_page_href = response.css(
                self.next_page_button_selector
            ).extract_first()
            if next_page_href:
                next_page_url = self.base_url + "/" + next_page_href
                yield response.follow(next_page_url, callback=self.parse)
