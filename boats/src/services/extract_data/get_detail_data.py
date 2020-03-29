def get_details(full_specs):
    details = dict()
    for line in full_specs:
        line = " ".join(line.split()).strip().lower()
        line = line.split(":")
        if len(line) == 2:
            key = line[0].strip()
            val = line[1].strip()
            if key and val:
                details[key] = val
        else:
            continue

    return details
