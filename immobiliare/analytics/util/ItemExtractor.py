from west.AbstractSpiderFactory import AbstractFieldExtractor


class EmlakItemExtractor(AbstractFieldExtractor):
    item_selectors = {
        "title": "td.searchResultsTitleValue a.classifiedTitle::text",
        "link": "td.searchResultsTitleValue a.classifiedTitle::attr(href)",
        "price": "td.searchResultsPriceValue div::text",
        "date": "td.searchResultsDateValue span:first-child::text",
        "year": "td.searchResultsDateValue span:last-child::text",
    }

    # basic field selectors
    def extract_item(self, row):
        item = dict()
        for field_key, selector in self.item_selectors.items():
            try:
                field_value = row.css(selector).extract_first()
                if field_value:
                    field_value = " ".join(field_value.split())
                else:
                    field_value = ""
            except TypeError:
                field_value = ""
            item[field_key] = field_value
        return item
