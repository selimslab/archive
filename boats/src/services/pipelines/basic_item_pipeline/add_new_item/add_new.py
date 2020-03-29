def add_new(item):
    maker_model_and_year = get_maker_model_and_year(item.get("link"))

    item.update(maker_model_and_year)

    city_state_and_country = extract_city_state_and_country_from_location(
        item.get("location")
    )

    item.update(city_state_and_country)

    item["price"] = clean_price(item.get("price"))

    item["first_seen"] = todays_date
    item["last_seen"] = todays_date

    item["days"] = 1

    db_api.save_new_item(item)

    return item
