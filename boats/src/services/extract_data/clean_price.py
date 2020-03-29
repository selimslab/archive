import re


def clean_price(dirty_price):
    price = None
    price_digits = re.findall(r"\d+", dirty_price)
    if price_digits:
        price = "".join(price_digits)
        try:
            price = int(price)
        except TypeError or ValueError:
            pass
    return price
