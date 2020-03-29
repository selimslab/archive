import datetime
from pprint import pprint
from ambrogio_mock import AmbrogioMock
from plugins.treasurer import Treasurer
from ambrogio.message import Message


class TestTresurer:
    plugin = None
    ambrogio = None

    def __init__(self):
        self.setup_method()

    def setup_method(self):
        self.ambrogio = AmbrogioMock()
        self.plugin = Treasurer()
        self.plugin.init_plugin(self.ambrogio)

    @staticmethod
    def create_message(text, sender=None):
        if sender is None:
            sender = "XY"
        mex = Message(text, sender, datetime.datetime.now())
        return mex

    def test_is_done(self, mex: Message):
        number_of_logs_before = len(self.ambrogio.logs)
        self.plugin.receive_message(self.ambrogio, mex)
        number_of_logs_after = len(self.ambrogio.logs)
        is_done = (
            "Done" == self.ambrogio.logs[-1]
            and number_of_logs_after == number_of_logs_before + 1
        )
        if not is_done:
            print(mex.text)
            pprint(self.ambrogio.logs)
        assert is_done

    def test_silence(self, mex):
        number_of_logs_before = len(self.ambrogio.logs)
        self.plugin.receive_message(self.ambrogio, mex)
        number_of_logs_after = len(self.ambrogio.logs)

        is_silent = number_of_logs_after == number_of_logs_before
        if not is_silent:
            print(mex.text)
            pprint(self.ambrogio.logs)
        assert is_silent

    def test_balance_answer(self, mex, answer):
        self.test_is_done(mex)
        mex = self.create_message("BALANCE")
        self.plugin.receive_message(self.ambrogio, mex)
        answer_in_logs = set(self.ambrogio.logs[-len(answer) :])
        is_correct_balance = answer == answer_in_logs
        if not is_correct_balance:
            print(mex.text)
            pprint(self.ambrogio.logs)
        assert is_correct_balance

    def test_add_expense(self):
        self.ambrogio.store = {}
        mex = self.create_message('40.00|LQ,FP,MD,GR "Dinner out"', "LQ")
        self.test_is_done(mex)

        should_fail = [
            # invalid name
            "210|GSGSD, XC",
            # invalid name
            "210|csdfsdf, XC",
            # " in message
            '40.00|LQ,FP,MD,GR "Dinn"er out"',
            # negative amount
            "-40.00|LQ,FP,MD,GR",
            # 0 amount
            "0|LQ,FP,MD,GR",
            # bad amount
            "gsgfsd |LQ,FP,MD,GR",
            "40,23|LQ,CD",
            # 3 decimal points
            "40.234|LQ,CD",
            # duplicate participant
            '40.00|LQ,FP,MD,GR, FP "Dinn"er out"',
            # bad modifiers
            "132|KC?54, PL- 67",
            "45|KC*+2, PL+3",
            "45|KC, PL+*3",
            # negative modifier
            "45|KC, PL*-3",
            # extra spaces
            " 45 | KC, PL  + 3  ",
            # bad handles
            '40.00|LQ,FP,MD,GM3 "Dinner out"',
            '40.00|LQ,FP,MD,GMF "Dinner out"',
        ]

        for text in should_fail:
            mex = self.create_message(text)
            self.test_silence(mex)

    def test_balance(self):
        self.ambrogio.store = {}
        mex = self.create_message("BALANCE")
        self.test_is_done(mex)

        mex = self.create_message('40.00|LQ,FP,MD,GR "Dinner out"', "LQ")
        answer = {"FP owes LQ 10.00", "MD owes LQ 10.00", "GR owes LQ 10.00"}
        self.test_balance_answer(mex, answer)

    def test_create_group(self):
        mex = self.create_message("CREATE SUSHILOVERS")
        self.test_is_done(mex)

        should_fail = [
            "CREATE SUSHILOVERS",
            "CREATE SDSGDSHDFHDFHDFDFHDF",
            "CREATE ss",
            "CREATE ASFSs",
            "CREATE ASF?H",
        ]
        for text in should_fail:
            mex = self.create_message(text)
            self.test_silence(mex)

    def test_add_to_group(self):
        mex = self.create_message("ADD LQ SUSHILOVERS")
        self.test_is_done(mex)

        # try to add existing member
        mex = self.create_message("ADD LQ SUSHILOVERS")
        self.test_silence(mex)

    def test_delete_from_group(self):
        mex = self.create_message("DELETE LQ SUSHILOVERS")
        self.test_is_done(mex)

        # try to delete  non-existing
        mex = self.create_message("DELETE NE SUSHILOVERS")
        self.test_silence(mex)

        mex = self.create_message("DELETE dasda SUSHILOVERS")
        self.test_silence(mex)

        mex = self.create_message("DELETE LQ SUSHI")
        self.test_silence(mex)

    def test_groups(self):
        self.test_create_group()
        self.test_add_to_group()
        self.test_delete_from_group()

    def test_history(self):
        self.ambrogio.store = {}
        mex = self.create_message("HISTORY")
        self.test_is_done(mex)

        mex = self.create_message('20|XX,MD "Hot chocolate"', "XX")
        self.test_is_done(mex)

        mex = self.create_message("21.90|LG,XX,SC", "LG")
        self.test_is_done(mex)

        mex = self.create_message('11.6|XX,MD "Beer"', "GP")
        self.test_is_done(mex)

        mex = self.create_message("HISTORY", "XX")
        self.plugin.receive_message(self.ambrogio, mex)
        today = datetime.datetime.now().strftime("%d/%m/%y")
        answer = [
            today + " Hot chocolate - you get back 10.00",
            today + "  - you pay back 7.30",
            today + " Beer - you pay back 5.80",
        ]
        assert answer == self.ambrogio.logs[-len(answer) :]

    def test_group_modifiers(self):
        self.ambrogio.store = {}

        mex = self.create_message("CREATE SUSHILOVERS")
        self.test_is_done(mex)

        mex = self.create_message("ADD MB SUSHILOVERS")
        self.test_is_done(mex)

        mex = self.create_message("ADD LQ SUSHILOVERS")
        self.test_is_done(mex)

        mex = self.create_message("ADD FP SUSHILOVERS")
        self.test_is_done(mex)

        mex = self.create_message("132|SUSHILOVERS+6,MD*2", "LQ")
        answer = {"FP owes LQ 28.80", "MB owes LQ 28.80", "MD owes LQ 45.60"}
        self.test_balance_answer(mex, answer)

    def test_modifiers(self):
        # normal case
        self.ambrogio.store = {}
        mex = self.create_message("62|MM+2*3,LQ*2,FP", "LQ")
        answer = {"FP owes LQ 10.00", "MM owes LQ 32.00"}
        self.test_balance_answer(mex, answer)

        # invalid statement
        mex = self.create_message("50|MD,FP+60", "MD")
        self.test_silence(mex)

        # groups
        self.test_group_modifiers()


if __name__ == "__main__":
    tester = TestTresurer()

    tester.test_add_expense()
    tester.test_balance()
    tester.test_history()
    tester.test_groups()
    tester.test_modifiers()
