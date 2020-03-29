import unittest

from src.services.extract_data import (
    extract_city_state_and_country_from_location,
    get_maker_model_and_year,
)
from src.services.pipelines.detail_item_pipeline.details import clean_details


class TestSum(unittest.TestCase):
    def test_basics(self):
        item = {
            "length": 128,
            "location": "valencia, spain",
            "sale_pending": None,
            "broker": "five seas yachts",
            "link": "https://www.yachtworld.com/boats/2008/maiora-39m-3483268/",
            "price": 7485240,
            "days": 1,
            "maker": "maiora",
            "model": "maiora 39m",
            "year": 2008,
            "city": "valencia",
            "state": "",
            "country": " spain",
            "first_seen": "2019-04-10",
            "last_seen": "2019-04-10",
        }

        city_state_and_country = {"city": "valencia", "state": "", "country": "spain"}
        maker_model_and_year = {"maker": "maiora", "model": "maiora 39m", "year": 2008}

        self.assertEqual(
            get_maker_model_and_year(item.get("link")), maker_model_and_year
        )
        self.assertEqual(
            extract_city_state_and_country_from_location(item.get("location")),
            city_state_and_country,
        )

    def test_details(self):
        details = {"total power": " 870 kw ", "engine hours": "     450     "}
        cleans = {"total power": int(870 * 1.341), "engine hours": 450}
        self.assertEqual(clean_details(details), cleans)


if __name__ == "__main__":
    unittest.main()
