def get_maker_model_and_year(link):
    # get the year and model from the link
    base_url = "https://www.yachtworld.com/boats/"

    split_link = link.replace(base_url, "").strip().split("/")
    year, model = split_link[0], split_link[1]
    model = model.split("-")

    maker = model[0]
    model = " ".join(model[:-1])  # the last element is irrelevant

    try:
        year = int(year)
    except TypeError or ValueError:
        pass

    return {"maker": maker, "model": model, "year": year}
