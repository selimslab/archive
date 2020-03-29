# -*- coding: utf-8 -*-
from ambrogio.plugin import Plugin


class Greeter(Plugin):
    def init_plugin(self, ambrogio):
        pass

    def receive_message(self, ambrogio, message):
        if message.text == "hi":
            ambrogio.send_text("hello {}".format(message.sender))
