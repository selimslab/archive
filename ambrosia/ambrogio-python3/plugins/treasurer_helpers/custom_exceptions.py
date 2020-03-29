"""
Custom exception classes,
they simplify error handling,
 and they keep us from having to propagate errors all the way down in call stack
"""


class InvalidMessage(Exception):
    pass


class InvalidHandle(Exception):
    pass


class GroupException(Exception):
    pass


class ModifierException(Exception):
    pass
