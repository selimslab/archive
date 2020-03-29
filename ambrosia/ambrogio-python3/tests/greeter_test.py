import datetime

from ambrogio.message import Message
from plugins.greeter import Greeter
from ambrogio_mock import AmbrogioMock


class TestGreater(object):
    plugin = None
    ambrogio = None

    def setup_method(self, f):
        self.ambrogio = AmbrogioMock()
        self.plugin = Greeter()
        self.plugin.init_plugin(self.ambrogio)

    def test_no_answer(self):
        mex = Message("yo", "MD", datetime.datetime.now())
        self.plugin.receive_message(self.ambrogio, mex)
        assert 0 == len(self.ambrogio.logs)

    def test_hello_MD(self):
        mex = Message("hi", "MD", datetime.datetime.now())
        self.plugin.receive_message(self.ambrogio, mex)
        assert 1 == len(self.ambrogio.logs)
        assert "hello MD" == self.ambrogio.logs[0]
