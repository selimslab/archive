# Python 3 Version

## Running Ambrogio

In order to run the Python version of Ambrogio, start up a terminal and type:

```
python3 main.py
```

after `cd`'ing into the `ambrogio-python` folder. You should see a ▶ symbol appear, prompting you to type your message to the bot.

## Modifying the code

The file you need to modify is `plugins/treasurer.py`. In particular you need to modify the methods `init_plugin` (called when the program is started, giving you a chance to execute any initialization code needed) and `receive_message` (called every time a message is sent to the bot).

The `Ambrogio` instance that you'll receive as a parameter to both methods exposes the methods `store_value`, `retrieve_value`, and `send_text`. The first two let you store and retrieve data by key, whereas the last one lets you print a message at the prompt, simulating a response from the bot. Feel free to read the documentation for those methods to better understand how they work.

The `Message` instance that you'll receive as a parameter to `receive_message` has the following,
self-explanatory properties:

- `sender`,
- `message`,
- `date`.

## Adding dependencies

It's fine if you feel like using external libraries to support your coding effort. Remember, though:

> With great power comes great responsibility.

In your case, this means that you should use `pip` to install your dependencies, and add them to a `requirements.txt` file that you'll place at the root of the `ambrogio-python` folder. Every line will correspond to a library. Here is a sample `requirements.txt` file:

```
requests
boto
pandas
Flask
```

If you do this, you'll make our life much easier. Before testing your implementation, we'll execute:

```
pip install -r requirements.txt
```

and we'll be all set, because all the dependencies that your code uses will be added to our environment.
