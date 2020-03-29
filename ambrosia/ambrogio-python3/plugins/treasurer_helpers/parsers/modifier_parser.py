import re
from plugins.treasurer_helpers.custom_exceptions import (
    InvalidMessage,
    ModifierException,
    InvalidHandle,
)
from plugins.treasurer_helpers.validator import Validator
from plugins.treasurer_helpers.convertor import Convertor


class ModifierParser:
    """
    a parser component to parse messages with modifiers
    """

    @staticmethod
    def parse_multiple_modifiers(modifier_string: str) -> dict:
        """
        :param modifier_string: "+2*3"
        :return: {"*": 3, "+": 2}
        """
        first_char = modifier_string[0]
        if first_char == "+":
            # "+2*3" -> ["+2", "3"]
            plus, star = modifier_string.split("*")
        elif first_char == "*":
            # "*4+5" -> ["*4", "5"]
            star, plus = modifier_string.split("+")
        else:
            raise ModifierException("check modifiers")

        if star and plus:
            star = star.replace("*", "").strip()
            plus = plus.replace("+", "").strip()

        sum_coeff = Convertor.str_to_decimal(plus)
        multiplication_coeff = Convertor.str_to_decimal(star)

        is_coeffs_valid = Validator.is_valid_number(
            multiplication_coeff
        ) and Validator.is_valid_number(sum_coeff)
        if not is_coeffs_valid:
            raise ModifierException("invalid coeffs")

        return {"*": multiplication_coeff, "+": sum_coeff}

    @staticmethod
    def parse_single_modifier(modifier_string: str) -> dict:
        """
        :param modifier_string: a string like  "+2"
        :return: {"+": 2}
        """
        first_char = modifier_string[0]
        if first_char not in {"+", "*"}:
            raise ModifierException("invalid modifier")

        if first_char == "+":
            num = Convertor.str_to_decimal(modifier_string.replace("+", "").strip())
            if not Validator.is_valid_number(num):
                raise ModifierException("invalid number")
            return {"+": num}

        elif first_char == "*":
            num = Convertor.str_to_decimal(modifier_string.replace("*", "").strip())
            if not Validator.is_valid_number(num):
                raise ModifierException("invalid number")
            return {"*": num}

    @staticmethod
    def parse_handles_with_modifiers(debtor_strings: list, groups: dict) -> list:
        """
        :param debtor_strings: ["MOVIEBUFF*2+3", "FC+2*6"]
        :param groups: {"MOVIEBUFF": {"LQ","KR"} }
        :return: person_handle_modifier_pairs [("LQ", "*2+3"), ("KR", "*2+3"), ("FC", "+2*6")]
        """
        person_handle_and_modifier_pairs = list()

        group_names = set(groups.keys())

        for debtor_string in debtor_strings:

            is_group = False
            is_person = False
            # group
            for group_name in group_names:
                if group_name in debtor_string:
                    people = groups.get(group_name, set())
                    modifier_string = debtor_string.replace(group_name, "").strip()
                    for person in people:
                        new_pair = (person, modifier_string)
                        person_handle_and_modifier_pairs.append(new_pair)

                    is_group = True
                    break

            if not is_group:
                # single person
                # match "LQ+" or "LQ*" or "LQ", don't match "LQW"
                handle_pattern = re.compile(r"^[A-Z]{2}\+|^[A-Z]{2}\*|^[A-Z]{2}$")
                handle_matches = handle_pattern.findall(debtor_string)

                if not len(handle_matches) == 1:
                    raise InvalidHandle("invalid handle")

                debtor_handle = handle_matches.pop()[:2]
                if not Validator.is_valid_person_handle(debtor_handle):
                    raise InvalidHandle("invalid handle")

                modifier_string = debtor_string.replace(debtor_handle, "").strip()
                new_pair = (debtor_handle, modifier_string)
                person_handle_and_modifier_pairs.append(new_pair)
                is_person = True

            if not (is_group or is_person):
                raise InvalidMessage("no debtor")

        return person_handle_and_modifier_pairs

    @classmethod
    def parse_modifier_coefficients(cls, modifier_string: str):
        # 1 piece by default
        default_coefficient = {"*": 1}
        if not modifier_string:
            return default_coefficient
        else:
            if "+" in modifier_string and "*" in modifier_string:
                coefficients = cls.parse_multiple_modifiers(modifier_string)
            else:
                coefficients = cls.parse_single_modifier(modifier_string)
            if not coefficients:
                raise ModifierException("invalid modifier")
            return coefficients
