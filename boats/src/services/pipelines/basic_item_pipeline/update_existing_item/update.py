def update(link, item):
    saved_item = db_api.get_saved_item(link)

    if not saved_item:
        item = add_new(item)
        return item

    last_seen = saved_item.get("last_seen")

    if last_seen and str_to_date(last_seen) == todays_date:
        return "already updated today"

    updates = get_updates(item, saved_item)

    return updates
