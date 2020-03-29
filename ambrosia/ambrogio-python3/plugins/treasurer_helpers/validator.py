import re
from decimal import Decimal


class Validator:
    @staticmethod
    def is_valid_person_handle(handle: str):
        """
        2 chars, all letters, uppercase
        """
        return len(handle) == 2 and handle.isalpha() and handle.isupper()

    @staticmethod
    def is_valid_number(number):
        is_at_most_2_decimal_digits = True
        if "." in str(number):
            is_at_most_2_decimal_digits = len(str(number).split(".")[1]) <= 2

        return (
            number is not None
            and number > 0
            and isinstance(number, Decimal)
            and is_at_most_2_decimal_digits
        )

    @staticmethod
    def is_debtors_valid(handles: list, groups: dict) -> bool:
        """
        all handles must be either group_name or person_handle
        """
        if not handles:
            return False

        group_names = set(groups.keys())

        is_all_handles_valid = all(
            [
                Validator.is_valid_person_handle(handle) or handle in group_names
                for handle in handles
            ]
        )

        no_repeating_participant = sorted(handles) == sorted(list(set(handles)))
        return is_all_handles_valid and no_repeating_participant

    @staticmethod
    def is_valid_group_name(group_name):
        # The group name should contain a minimum of 3 and a maximum of 12 capital letters.
        is_valid_length = 3 <= len(group_name) <= 12

        # No space, number, or special characters are allowed.
        is_all_chars_valid = re.sub("[^A-Za-z]+", "", group_name) == group_name

        return (
            group_name.isalpha()
            and group_name.isupper()
            and is_valid_length
            and is_all_chars_valid
        )
