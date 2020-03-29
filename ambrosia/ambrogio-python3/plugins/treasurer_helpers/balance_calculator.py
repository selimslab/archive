"""
Figure out minimum number of necessary money transfers to settle all debt
"""


from decimal import getcontext

# 10 digits decimal precision
getcontext().prec = 10


class BalanceCalculator:
    @staticmethod
    def update_balance(balance: dict, new_transaction: dict):
        # new_transaction dict is safe, it is checked on creation
        creditor = new_transaction.get("creditor")
        amount = new_transaction.get("amount")
        debt_per_person = new_transaction.get("debts")

        balance[creditor] = balance.get(creditor, 0) + amount
        for person, debt in debt_per_person.items():
            balance[person] = balance.get(person, 0) - debt
        return balance

    @staticmethod
    def get_cash_flow_operations(balance: dict, operations: list) -> list:
        """
        Greedy recursive min cash flow algorithm, 0(n^2), not guaranteed for large numbers
        :param balance: final debit or credit for every participant ever seen
        :param operations: []
        :return: operations: a list of payments [ (debtor, creditor, amount) ... ]
        """
        if not balance:
            return []
        # find max_debtor, max_creditor, and their balances
        try:
            max_debtor = min(balance, key=balance.get)
            max_creditor = max(balance, key=balance.get)
        except ValueError:
            raise

        max_credit = balance.get(max_creditor, 0)
        max_debit = balance.get(max_debtor, 0)

        # If both are close to 0, then all settled
        epsilon = 0.001
        if max_credit <= epsilon and max_debit <= epsilon:
            return operations

        # settle these 2 people
        to_be_paid_of = min(-max_debit, max_credit)

        if to_be_paid_of <= epsilon:
            return operations

        balance[max_creditor] -= to_be_paid_of
        balance[max_debtor] += to_be_paid_of

        new_operation = (max_debtor, max_creditor, to_be_paid_of)
        operations.append(new_operation)

        # recur until all settles
        return BalanceCalculator.get_cash_flow_operations(balance, operations)
