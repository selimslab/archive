# -*- coding: utf-8 -*-


class Message(object):
    """Class representing a message received by Ambrogio."""

    def __init__(self, text, sender, date):
        """
        :param text: the message body.
        :type text: str
        :param sender: the message sender, represented by their initials (e.g. XY).
        :type sender: str
        :param date: the date and time when the message was received.
        :type date: datetime.datetime
        """
        self.text = text
        self.sender = sender
        self.date = date
