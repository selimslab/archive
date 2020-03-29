# -*- coding: utf-8 -*-
from datetime import datetime
import re
from ambrogio.message import Message


class Ambrogio(object):
    """Class representing a bot."""

    DEFAULT_USER = "XY"
    un_pattern = re.compile("^([A-Z]{2}): (.+)$")

    def __init__(self, plugins=None):
        """
        :param plugins: a list of plugins that should process messages received by the bot.
        :type plugins: list[ambrogio.plugin.Plugin]
        """
        self._plugin_classes = plugins or []
        self.set_initial_store()

    def set_initial_store(self):
        self.store = {}

    def load_plugins(self):
        self.plugins = [Plugin() for Plugin in self._plugin_classes]
        for plugin in self.plugins:
            plugin.init_plugin(self)

    def _split_message(self, message):
        m = self.un_pattern.match(message)
        if m:
            return m.groups()
        return self.DEFAULT_USER, message

    def run(self):
        """Run the bot logic, wait for user input, and process
        that input with the loaded plugins."""
        self.load_plugins()
        while True:
            raw_message = input("▶ ")
            sender, text = self._split_message(raw_message)
            message = Message(text, sender, datetime.utcnow())
            self.handle_message(message)

    def handle_message(self, message):
        """:type message: ambrogio.message.Message"""

        # handle debugging messages
        if message.text == "!RELOAD_PLUGINS":
            self.handle_reload_plugins()
            return
        if message.text == "!RESET_STORE":
            self.handle_reset_store()
            return

        if not message.sender == self.DEFAULT_USER:
            print("[👤 sent by {}]".format(message.sender))

        for plugin in self.plugins:
            plugin.receive_message(self, message)

    def send_text(self, text):
        """Send some text to Ambrogio's output stream."""
        print("↪ {}".format(text))

    def store_value(self, key, value):
        """Store a value corresponding to a given key.

        :param key: the key to which this `value` corresponds.
        :type key: str
        :param value: the value corresponding to `key`.
        """
        self.store[key] = value

    def retrieve_value(self, key):
        """Retrieve the value associated to a given key.

        :param key: the key whose associated value is to be retrieved.
        :type key: str
        :returns: the associated value, or None if no value had been saved for that key before.
        """
        return self.store.get(key)

    def handle_reload_plugins(self):
        self.load_plugins()

    def handle_reset_store(self):
        self.set_initial_store()
