from west.AbstractSpiderFactory import AbstractSpider

from scrapy import Request


class EmlakSpider(AbstractSpider):
    name = "emlak"
    start_urls = list()
    base_url = "https://www.sahibinden.com"

    custom_settings = {
        "ITEM_PIPELINES": {
            "projects.spiders.emlak.pipelines.EmlakPipeline": 200,
            # the number in range 0-1000
        }
    }

    # entry point
    def __init__(self, *args, **kwargs):
        super(EmlakSpider, self).__init__(*args, **kwargs)
        self.url_generator = EmlakURLGenerator()
        self.item_extractor = EmlakItemExtractor()

    # Send urls to parse
    def start_requests(self):
        for url in self.url_generator.url_generator():
            yield Request(url=url, callback=self.parse)

    def parse(self, response):
        # define the data to process
        search_results_table_selector = "tbody.searchResultsRowClass"
        search_results_table = response.css(search_results_table_selector)
        for row in search_results_table:
            item = self.item_extractor.extract_item(row)
            yield item

        # follow to the next page
        if self.next_page:
            next_page_button_selector = (
                "ul.pageNaviButtons > li:last-child > a::attr(href)"
            )
            next_page_href = response.css(next_page_button_selector).extract_first()
            if next_page_href is not None:
                next_page_url = self.base_url + next_page_href
                yield response.follow(
                    next_page_url, meta=response.meta, callback=self.parse
                )
