# -*- coding: utf-8 -*-
from decimal import InvalidOperation
from ambrogio.plugin import Plugin
from plugins.treasurer_helpers.bill_splitter import BillSplitter
from plugins.treasurer_helpers.balance_calculator import BalanceCalculator
from plugins.treasurer_helpers.parsers.parser import Parser
from plugins.treasurer_helpers.group_manager import GroupManager
from plugins.treasurer_helpers.custom_exceptions import (
    InvalidMessage,
    ModifierException,
    InvalidHandle,
    GroupException,
)


class Treasurer(Plugin):
    """
    Keeps track of group expenses,
    Provide a report of the current financial situation
    Provide transaction history
    """

    def __init__(self):
        self.ambrogio = None

        self.group_manager = GroupManager()

        self.commands = {
            "|": self.add_expense,
            "BALANCE": self.show_balance,
            "HISTORY": self.show_history,
            "CREATE": self.group_manager.create_group,
            "ADD": self.group_manager.add_to_group,
            "DELETE": self.group_manager.delete_from_group,
        }
        self.group_commands = {"CREATE", "ADD", "DELETE"}

        self.parser = Parser()
        self.bill_splitter = BillSplitter()
        self.balance_calculator = BalanceCalculator()

        self.balance = {}  # final debit or credit for every participant ever seen
        self.transactions = []  # [ (creditor, amount, debts, date, description) ... ]

    def init_plugin(self, ambrogio):
        self.ambrogio = ambrogio

    def retrieve_with_default_value(self, key, default_value):
        value = self.ambrogio.retrieve_value(key)
        if value is None:
            value = default_value
            self.ambrogio.store_value(key, default_value)
        return value

    def create_new_transaction(self, message):
        try:
            (
                creditor,
                amount,
                debtor_strings,
                description,
            ) = self.parser.parse_expense_message(message)
        except ValueError:
            raise

        groups = self.retrieve_with_default_value("groups", dict())
        debt_per_person: dict = BillSplitter.split_the_bill(
            debtor_strings, amount, groups
        )

        if not debt_per_person:
            raise InvalidMessage("invalid debts")

        new_transaction = {
            "creditor": creditor,
            "amount": amount,
            "debts": debt_per_person,
            "date": message.date.strftime("%d/%m/%y"),
        }
        if description:
            new_transaction["description"] = description

        return new_transaction

    def add_expense(self, message):
        new_transaction = self.create_new_transaction(message)

        self.transactions = self.retrieve_with_default_value("transactions", list())
        self.transactions.append(new_transaction)
        self.ambrogio.store_value("transactions", self.transactions)

        old_balance = self.retrieve_with_default_value("balance", dict())
        new_balance = self.balance_calculator.update_balance(
            old_balance, new_transaction
        )
        self.ambrogio.store_value("balance", new_balance)

        return "Done"

    def show_history(self, message):
        transactions = self.retrieve_with_default_value("transactions", list())
        if not transactions:
            return "Done"

        sender = message.sender
        history_lines = list()
        for transaction in transactions:
            credit = 0
            if sender == transaction["creditor"]:
                credit = transaction.get("amount", 0)

            debit = transaction["debts"].get(sender, 0)
            due = credit - debit

            history_line = [transaction["date"], transaction.get("description", "")]

            if due > 0:
                history_line += ["- you get back", f"{due:.{2}f}"]
            elif due < 0:
                history_line += ["- you pay back", f"{-due:.{2}f}"]
            else:
                # don't show if 0
                continue

            history_line = " ".join(history_line)
            history_lines.append(history_line)

        for history_line in history_lines:
            self.ambrogio.send_text("{}".format(history_line))
        if not history_lines:
            return "Done"

    def show_balance(self, message):
        balance = self.retrieve_with_default_value("balance", dict())
        cash_flow_operations = self.balance_calculator.get_cash_flow_operations(
            balance.copy(), operations=[]
        )
        if not cash_flow_operations:
            return "Done"

        self.ambrogio.send_text("{}: BALANCE".format(message.sender))
        for (debtor, creditor, amount) in cash_flow_operations:
            balance_line = " ".join([debtor, "owes", creditor, f"{amount:.{2}f}"])
            self.ambrogio.send_text("{}".format(balance_line))

    @staticmethod
    def safe_execute(func, *args):
        """
        Run a function with arguments and handle exceptions
        """
        try:
            return func(*args)
        # pass to fail silently
        except InvalidMessage as e:
            pass
        except InvalidHandle as e:
            pass
        except ModifierException as e:
            pass
        except InvalidOperation as e:
            pass
        except GroupException as e:
            pass
        except RecursionError as e:
            pass
        except ValueError as e:
            pass

    def receive_message(self, ambrogio, message):
        """
        if a valid command is found in message,
        send the message to the related function
        """
        for command, func in self.commands.items():
            if command in message.text:

                if command in self.group_commands:
                    groups = self.retrieve_with_default_value("groups", dict())
                    # on success, group functions return the updated groups dict
                    updated_groups = self.safe_execute(func, groups, message)
                    if updated_groups:
                        self.ambrogio.store_value("groups", updated_groups)
                        ambrogio.send_text("Done")

                else:
                    result = self.safe_execute(func, message)
                    if result:
                        ambrogio.send_text(result)

                break
