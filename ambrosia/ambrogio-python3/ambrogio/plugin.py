# -*- coding: utf-8 -*-
from abc import ABCMeta, abstractmethod


class Plugin(object, metaclass=ABCMeta):
    @abstractmethod
    def init_plugin(self, ambrogio):
        """Perform any initialization operations when the bot starts.

        :param ambrogio: the bot that processes user requests.
        :type ambrogio: ambrogio.bot.Ambrogio
        """
        pass

    @abstractmethod
    def receive_message(self, ambrogio, message):
        """Process a message received by a user.

        :param ambrogio: the bot that processes user requests.
        :type ambrogio: ambrogio.bot.Ambrogio
        :param message: the received message.
        :type message: ambrogio.message.Message
        """
        pass
