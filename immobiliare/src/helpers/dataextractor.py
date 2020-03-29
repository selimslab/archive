import re


class DataExtractor(object):
    @staticmethod
    def remove_whitespace(str):
        return "".join(str.split())

    @staticmethod
    def extract_details(response):
        details_list = response.css("ul.classifiedInfoList li")
        details = dict()
        numeric_values = {
            "İlan No",
            "m² (Net)",
            "m² (Brüt)",
            "Bina Yaşı",
            "Kat Sayısı",
            "Bulunduğu Kat",
            "Banyo Sayısı",
            "Aidat (TL)",
        }
        for row in details_list:
            key = row.css("strong::text").extract_first().strip()
            value = row.css("span::text").extract_first().strip()
            if key in numeric_values:
                try:
                    value = int(value)
                except ValueError:
                    pass
            details[key] = value

        return details

    @staticmethod
    def extract_images(response):
        thumbnails = response.css(".classifiedDetailThumbListPages img::attr(src)")
        image_srcs = list()
        for tb in thumbnails:
            thumbnail_src = tb.extract()
            image_src = thumbnail_src.replace("thmb", "x5")
            image_srcs.append(image_src)

        return image_srcs

    @staticmethod
    def extract_price(response):
        price = response.css("div.classifiedInfo h3::text").extract_first()
        price = price.replace("TL", "").replace(".", "")
        price = DataExtractor.remove_whitespace(price)
        return int(price)

    @staticmethod
    def extract_il_ilce_semt(response):
        selectors = {
            "il": {
                "name": "div.classifiedInfo h2 a:nth-child(1)::text",
                "link": "div.classifiedInfo h2 a:nth-child(1)::attr(href)",
            },
            "ilce": {
                "name": "div.classifiedInfo h2 a:nth-child(3)::text",
                "link": "div.classifiedInfo h2 a:nth-child(3)::attr(href)",
            },
            "semt": {
                "name": "div.classifiedInfo h2 a:nth-child(5)::text",
                "link": "div.classifiedInfo h2 a:nth-child(5)::attr(href)",
            },
        }

        il_ilce_semt = dict()
        benzer_ilanlar = dict()
        for key, selector in selectors.items():
            name = response.css(selector["name"]).extract_first()
            name = DataExtractor.remove_whitespace(name)
            il_ilce_semt[key] = name
            link = response.css(selector["link"]).extract_first()
            benzer_ilanlar[key] = link

        return il_ilce_semt, benzer_ilanlar

    @staticmethod
    def extract_coordinates(response):
        coordinates = list()
        lat = response.css("div#gmap::attr(data-lat)").extract_first()
        lon = response.css("div#gmap::attr(data-lon)").extract_first()
        if lat and lon:
            coordinates = [float(lon), float(lat)]
        return {"type": "Point", "coordinates": coordinates}

    @staticmethod
    def extract_office_info(response):
        office = dict()
        office["name"] = (
            response.css("div.storeBox a span::text").extract_first().strip()
        )
        office["broker"] = (
            response.css("div.username-info-area h5::text").extract_first().strip()
        )
        office_url = response.css("div.storeBox a::attr(href)").extract_first()
        office["link"] = office_url
        office["subdomain"] = office_url.replace("https://", "").split(".")[0]
        phones = response.css("#phoneInfoPart li")
        for row in phones:
            print(row)
            key = row.css("strong::text").extract_first().strip()
            value = row.css("span.pretty-phone-part::text").extract_first().strip()
            office[key] = value
        return office

    @staticmethod
    def extract_detail_page(response):
        item = dict()

        item["title"] = (
            response.css("div.classifiedDetailTitle h1::text").extract_first().strip()
        )

        ds = response.xpath(
            '//*[@id="classifiedDescription"]/descendant::*/text()'
        ).extract()

        description = " ".join([line.replace(u"\xa0", "<br/>") for line in ds])
        # response.xpath('normalize-space(//*[@id="classifiedDescription"])').extract_first()

        item["description"] = description

        item["office"] = DataExtractor.extract_office_info(response)

        item["price"] = DataExtractor.extract_price(response)

        il_ilce_semt, benzer_ilanlar = DataExtractor.extract_il_ilce_semt(response)
        item.update(il_ilce_semt)
        item["benzer_ilanlar"] = benzer_ilanlar

        item["location"] = DataExtractor.extract_coordinates(response)

        item["details"] = DataExtractor.extract_details(response)

        item["images"] = DataExtractor.extract_images(response)

        return item
