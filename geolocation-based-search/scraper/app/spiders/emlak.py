# -*- coding: utf-8 -*-
import scrapy


class SahibindenSpider(scrapy.Spider):
    name = "emlak"
    allowed_domains = ["sahibinden.com"]

    # Configure item pipelines
    # See https://doc.scrapy.org/en/latest/topics/item-pipeline.html
    custom_settings = {
        "ITEM_PIPELINES": {
            "app.pipelines.DuplicatesPipeline": 200,  # the number in range 0-1000
            "app.pipelines.MongoPipeline": 300,  # pipeline with smaller number executed first
        }
    }

    def __init__(self, urls, pagination, coordinates, *args, **kwargs):
        super(SahibindenSpider, self).__init__(*args, **kwargs)
        self.start_urls = [urls]
        self.base_url = "https://www.sahibinden.com"
        if coordinates == "True" or "true":
            self.should_get_coordinates = True
        else:
            self.should_get_coordinates = False

        if pagination == "True" or "true":
            self.should_paginate = True
        else:
            self.should_paginate = False

        self.logger.info("%s\n\n\n" % type(coordinates))
        self.logger.info("%s\n\n\n" % type(self.should_get_coordinates))

    def parse(self, response):
        SET_SELECTOR = "tbody.searchResultsRowClass"

        ads = response.css(SET_SELECTOR)

        for ad in ads:

            TITLE_SELECTOR = "td.searchResultsTitleValue a.classifiedTitle::text"
            LINK_SELECTOR = "td.searchResultsTitleValue a.classifiedTitle::attr(href)"

            PRICE_SELECTOR = "td.searchResultsPriceValue div::text"

            DATE_SELECTOR = "td.searchResultsDateValue span:first-child::text"
            YEAR_SELECTOR = "td.searchResultsDateValue span:last-child::text"

            # TOWN_SELECTOR = 'td.searchResultsLocationValue'

            titles = ad.css(TITLE_SELECTOR).extract()
            links = ad.css(LINK_SELECTOR).extract()

            prices = ad.css(PRICE_SELECTOR).extract()

            dates = ad.css(DATE_SELECTOR).extract()
            years = ad.css(YEAR_SELECTOR).extract()

            # LOGGING EXAMPLE
            # self.logger.info('%s %s %s %s %s %s' % (titles,links, prices, dates, years, towns) )

            # iterate through items
            for title, link, price, date, year in zip(
                titles, links, prices, dates, years
            ):
                link = self.base_url + link
                # go to the item page to get coordinates
                if self.should_get_coordinates == True:
                    yield scrapy.Request(
                        link,
                        callback=self.parseCoordinates,
                        meta={
                            "title": title,
                            "link": link,
                            "price": price,
                            "date": date,
                            "year": year,
                        },
                    )
                else:
                    yield {
                        "title": title.strip(),
                        "link": link,
                        "date": date,
                        "year": year,
                        "price": price,
                    }

        if self.should_paginate == True:
            next_page = response.css(
                "ul.pageNaviButtons > li:last-child > a::attr(href)"
            ).extract_first()
            if next_page is not None:
                yield response.follow(next_page, callback=self.parse)

    def parseCoordinates(self, response):
        LAT_SELECTOR = "div#gmap::attr(data-lat)"
        LON_SELECTOR = "div#gmap::attr(data-lon)"

        latitude = response.css(LAT_SELECTOR).extract_first()
        longitude = response.css(LON_SELECTOR).extract_first()

        coordinates = []
        if latitude and longitude:
            coordinates = [float(longitude), float(latitude)]

        title = response.meta["title"]
        link = response.meta["link"]
        price = response.meta["price"]
        date = response.meta["date"]
        year = response.meta["year"]

        yield {
            "title": title.strip(),
            "link": link,
            "location": {"type": "Point", "coordinates": coordinates},
            "date": date,
            "year": year,
            "price": price,
        }
