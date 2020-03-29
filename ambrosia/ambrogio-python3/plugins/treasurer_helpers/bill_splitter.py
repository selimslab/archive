"""
Determine debt and credit for everyone in this transaction
"""
from decimal import Decimal
from plugins.treasurer_helpers.parsers.parser import Parser
from plugins.treasurer_helpers.validator import Validator

from plugins.treasurer_helpers.custom_exceptions import ModifierException, InvalidHandle


class BillSplitter:
    """
    Determine debt and credit for everyone in a transaction
    """

    @staticmethod
    def uneven_split(modifier_coefficients: dict, amount: Decimal) -> dict:
        """
        find debt_per_person when message includes a modifier
        """
        debt_per_person = dict()

        total_multiplier = 0
        modifier_addition = 0
        for (debtor, coefficients) in modifier_coefficients.items():
            total_multiplier += coefficients.get("*", 1)
            modifier_addition += coefficients.get("+", 0)

        if modifier_addition >= amount:
            # "MD: 50 | MD, FP + 60"
            raise ModifierException("modifier addition > amount")

        unit_debt = (amount - modifier_addition) / total_multiplier
        for (debtor, coefficients) in modifier_coefficients.items():
            multiplier = coefficients.get("*", 1)
            addition = coefficients.get("+", 0)
            debt_for_this_participant = unit_debt * multiplier + addition
            debt_per_person[debtor] = debt_for_this_participant

        return debt_per_person

    @staticmethod
    def even_split(person_handles: list, amount: Decimal) -> dict:
        """
        no modifiers
        """
        debt_per_person = {}
        number_of_debtors = len(person_handles)
        debt_for_one_person = amount / number_of_debtors
        for handle in person_handles:
            debt_per_person[handle] = debt_for_one_person
        return debt_per_person

    @classmethod
    def split_the_bill(
        cls, debtor_strings: list, amount: Decimal, groups: dict
    ) -> dict:
        """
        :param debtor_strings: ["MOVIEBUFF*2+3", "FC"]
        :param amount: 24.00
        :param groups: {"MOVIEBUFF": {"LQ","KR"} }
        :return: debt_per_person: {<HANDLE>:<DEBT> ... }
        """
        any_modifier = any(
            ["+" in debtor or "*" in debtor for debtor in debtor_strings]
        )
        if not any_modifier:
            people_handles = Parser.replace_group_handle_with_people_handles(
                debtor_strings, groups
            )
            if not Validator.is_debtors_valid(people_handles, groups):
                raise InvalidHandle("invalid handle")
            debt_per_person = cls.even_split(people_handles, amount)
        else:
            person_handle_and_modifier_pairs: list = Parser.parse_handles_with_modifiers(
                debtor_strings, groups
            )
            people_handles = [
                handle for (handle, modifier) in person_handle_and_modifier_pairs
            ]
            if not Validator.is_debtors_valid(people_handles, groups):
                raise InvalidHandle("invalid handle")

            modifier_coefficients = dict()
            for (handle, modifier_string) in person_handle_and_modifier_pairs:
                modifier_coefficients[handle] = Parser.parse_modifier_coefficients(
                    modifier_string
                )

            debt_per_person = cls.uneven_split(modifier_coefficients, amount)
        return debt_per_person
