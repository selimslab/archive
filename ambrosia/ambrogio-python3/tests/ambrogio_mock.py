from ambrogio.bot import Ambrogio


class AmbrogioMock(Ambrogio):
    def __init__(self):
        super(AmbrogioMock, self).__init__()
        self.logs = []

    def send_text(self, text):
        """Send some text to Ambrogio's output stream."""
        self.logs.append(text)
