def extract_city_state_and_country_from_location(location):
    location_parts = location.split(",")

    country = location_parts[-1].strip()
    city = location_parts[0].strip()

    if len(location_parts) == 3:
        state = location_parts[2].strip()
    else:
        state = ""

    return {"city": city, "state": state, "country": country}
