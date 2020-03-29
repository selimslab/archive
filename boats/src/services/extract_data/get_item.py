import re

field_selectors = {
    "length": "div.make-model a span.length::text",
    "location": "div.location::text",
    "sale_pending": "div.location span.active_field::text",
    "broker": "div.broker::text",
    "link": "div.make-model a::attr(href)",
    "price": "div.price::text",
}


def extract(key, row, selector):
    val = ""
    try:
        val = row.css(selector).extract_first()
        if val:
            val = " ".join(val.split()).strip().lower()
            if key == "length":
                digits_list = re.findall(r"\d+", val)
                digits = "".join(digits_list)
                val = int(digits)

    except TypeError or ValueError:
        pass
    finally:
        return val


def get_item(row):
    item = dict()
    for key, selector in field_selectors.items():
        item[key] = extract(key, row, selector)
    return item
