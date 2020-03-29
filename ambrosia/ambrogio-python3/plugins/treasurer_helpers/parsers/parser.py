from plugins.treasurer_helpers.parsers.modifier_parser import ModifierParser
from plugins.treasurer_helpers.parsers.expense_parser import ExpenseParser
from plugins.treasurer_helpers.parsers.handle_parser import HandleParser


class Parser(ExpenseParser, ModifierParser, HandleParser):
    pass
