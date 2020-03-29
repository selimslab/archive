from decimal import Decimal, InvalidOperation, getcontext

# 10 digits decimal precision
getcontext().prec = 10


class Convertor:
    @staticmethod
    def str_to_decimal(s: str):
        """
        Integers are also represented as python Decimal
        """
        try:
            return Decimal(s)
        except InvalidOperation as e:
            raise
