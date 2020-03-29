"""
a parser component to parse add_expense message
"""
import re
from typing import Tuple

from plugins.treasurer_helpers.custom_exceptions import InvalidMessage, InvalidHandle
from plugins.treasurer_helpers.validator import Validator
from plugins.treasurer_helpers.convertor import Convertor


class ExpenseParser:
    """
    Parse add_expense message
    """

    @staticmethod
    def parse_expense_message(message) -> Tuple:
        text = message.text.strip()

        amount, debtors_and_message = text.split("|")

        debtors_part = debtors_and_message.split('"')[0]

        debtor_strings = debtors_part.split(",")

        debtor_strings = [ds.strip() for ds in debtor_strings]

        creditor = message.sender
        if not Validator.is_valid_person_handle(creditor):
            raise InvalidHandle("invalid handle")

        amount = Convertor.str_to_decimal(amount.strip())
        if not Validator.is_valid_number(amount):
            raise InvalidMessage("invalid amount")

        # The message, if present, is enclosed in double quotes (").
        quotes_in_message = re.findall(r"\"", debtors_and_message)
        is_two_quotes_or_no_quote = (
            len(quotes_in_message) == 2 or len(quotes_in_message) == 0
        )
        # No double quotes are allowed in the message.
        if not is_two_quotes_or_no_quote:
            raise InvalidMessage("invalid message")

        description = None
        quoted_strings = re.findall(r"\"(.+?)\"", debtors_and_message)
        if quoted_strings:
            if len(quoted_strings) == 1:
                description = quoted_strings.pop()
            else:
                raise InvalidMessage("invalid quotes")

        return creditor, amount, debtor_strings, description
