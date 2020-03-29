# -*- coding: utf-8 -*-
from ambrogio.bot import Ambrogio
from plugins.greeter import Greeter
from plugins.treasurer import Treasurer


if __name__ == "__main__":
    ambrogio = Ambrogio(plugins=[Greeter, Treasurer])
    ambrogio.run()
