import re


def clean_details(details):
    integer_fields = {"engine hours", "total power"}
    for key, val in details.items():
        if key in integer_fields:
            try:
                details[key] = int(re.findall(r"\d+", val)[0])
                if key == "total power" and "kw" in val:
                    details[key] = int(1.341 * details.get(key))
            except TypeError:
                pass

    return details
