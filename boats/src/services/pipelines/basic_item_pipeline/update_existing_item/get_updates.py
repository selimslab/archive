def get_updates(item, saved_item):
    updates = {}

    new_price = item.get("price")
    old_price = saved_item.get("price")
    sale_pending = item.get("sale_pending")

    if old_price != new_price:
        updates = {
            "old_price": old_price,
            "price": clean_price(new_price),
            "price_changed": todays_date,
        }

    updates["last_seen"] = todays_date
    updates["removed"] = False

    if sale_pending:
        updates["sale_pending"] = todays_date

    return updates
