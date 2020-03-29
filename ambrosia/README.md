# Ready. Set. Hack!

Kudos! You made it so far. Now comes the fun part! It's time for you to show off your coding skills.

## The challenge

We're addicted [Slack](https://slack.com) users. Slack allows to create a private chat for you and your team. We use it to [coordinate](https://cloud.githubusercontent.com/assets/110147/9568395/4937aeca-4f48-11e5-9a26-6fab4ee0a7b1.png), to [share news](https://cloud.githubusercontent.com/assets/110147/9568393/4931f6a6-4f48-11e5-9fca-81a8db69ae9d.png), and to [have fun](https://cloud.githubusercontent.com/assets/110147/9568396/4937f592-4f48-11e5-9151-ee856e42704e.png).

When we discovered that Slack has an API we were really thrilled, and during a fun hackathon we created a bot to help us with many menial tasks like [managing provisioning profiles](https://cloud.githubusercontent.com/assets/110147/9568394/493568ea-4f48-11e5-8753-61fa9c30a2ad.png) 🙀. We think it's very cool. We called the bot Ambrogio.

At this point you should also know that we travel the world and we spend a considerable amount of time together. As any group of people hanging out together we've often faced the split-the-bill problem. Consider this scenario: you go out with your team, you eat some sushi, and one of you pays for that. Later when it's time to go back home, you order an Uber. Once again someone needs to cover the fee. How do you keep track of who paid for what? You can play the "it doesn't matter" game but it won't last for long: after all in any group there's always the generous one and the cheap one. We always wanted to find a convenient solution to this problem.

As for anything else, "there is an app for that", [Splitwise](https://itunes.apple.com/us/app/splitwise-split-bills-expenses/id458023433?mt=8). We think it's nice, but we'd like something cooler and geekier. Wouldn't it be cool to use our Slack bot to keep track of our expenses 😎?

## How to proceed

To simplify your task we created a simple bot that operates in a sandbox. We implemented it in 6 of the [top 20](https://www.tiobe.com/tiobe-index/) most-used programming languages:

- [Objective-C](ambrogio-objc/),
- [Swift](ambrogio-swift/),
- [JavaScript](ambrogio-js/),
- Python ([Python 2](ambrogio-python2/), [Python 3](ambrogio-python3/)),
- [Java](ambrogio-java/),
- [Ruby](ambrogio-ruby/),
- [C# (.NET Core)](ambrogio-dotnet-core/).

Pick the one you know best. In each of these projects we modeled the following high-level concepts:

- Ambrogio. The bot itself. It exposes methods to send messages and to store and retrieve values.
- Message. A message received by the bot, containing a sender, some text, and a date.
- Plugin. A plugin that can react when a message is received.

We've already coded two simple plugins. The first one is called `Greeter` and simply replies `hello {sender}` when someone sends `hi`. The second one is called `Treasurer` and it's the one you should work on.

The structure for the plugin is fairly simple, as you can see from the following Objective-C example:

```objective-c
@implementation AMTreasurer

- (void)ambrogioDidLoadPlugin:(AMAmbrogio*)ambrogio {
}

- (void)ambrogio:(AMAmbrogio*)ambrogio
      didReceiveMessage:(AMMessage*)message {
}

@end
```

There are two methods you need to implement. The first one is triggered when the plugin is loaded, the second one is triggered every time a message is received. The object modeling the bot is passed as parameter to both: use it to reply and to store/retrieve values.

In this development environment the bot uses the standard input/output and in-memory storage. In the test environment we might use different strategies, so make sure you:

- Don't modify anything else but the Treasurer plugin.
- Always and only use the provided API to store and retrieve the state, and to communicate with the user.

Note that your plugin could be reloaded by Ambrogio any time, and that such reloads should have no effect on subsequent commands.

Although you shouldn't edit or remove files except for the one containing the Treasurer plugin and the TreasurerTest one, feel free to add files to better modularize your code.

## How to run Ambrogio

If you run any of these projects, the bot will start and you'll be able to send and receive messages. By default, all the messages you type will be sent by the default user XY. In case you want to simulate a different one, use as prefix two capital letters followed by colon and space.

An example of interaction follows:

```
▶ hi
↪ hello XY
▶ LQ: hi
[👤 sent by LQ]
↪ hello LQ
```

## The treasurer

The plugin you need to implement, also known as Treasurer, allows to keep track of group expenses, and provides a report of the current financial situation of the group of people using the plugin, with details of who owes what to whom. A sample interaction between a group of users and Treasurer follows.

Imagine that LQ had dinner with FP, MD, and GR. LQ paid the bill, which was $40, and then proceeded to contact Treasurer with details of his payment:

```
▶ LQ: 40.00|LQ,FP,MD,GR "Dinner out"
[👤 sent by LQ]
↪ Done
```

Treasurer knows that it was LQ to pay, because the message was sent by him. Treasurer also knows that the bill should be evenly split between LQ, FP, MD, and GR.

After registering the transaction, LQ wants to know about the group's financial situation:

```
▶ LQ: BALANCE
[👤 sent by LQ]
↪ FP owes LQ 10.00
MD owes LQ 10.00
GR owes LQ 10.00
```

Later, GR pays $15 for the taxi ride home, which he shared with LQ and MD:

```
▶ GR: 15.00|GR,LQ,MD "Uber"
[👤 sent by GR]
↪ Done
▶ GR: BALANCE
[👤 sent by GR]
↪ MD owes LQ 15.00
FP owes LQ 10.00
```

As you can see, GR doesn't appear in the list anymore. That's because GR had a debt of $10 at first, but then paid $10 for LQ and MD, rendering his financial situation neutral with respect to the group.

## Requirements

Now that the overall concept should be clear, let's go through the specifics of the features we'd like you to implement.

Before presenting the different commands your bot should react to, we'd like to mention that any message the bot receives will lead to one of these two outcomes:

- Successful: the command belongs to the list below. It is well-formatted and the data that comes with it meets the requirements specified below (strings have the right length, numbers have the right number of decimals, there are no extra spaces between parts of the message, etc.).
- Unsuccessful: either the command isn't among the commands listed below, or it is, but it's wrongly-formatted, and/or the data doesn't meet the outlined requirements.

In case the outcome is successful, your bot should **always send a message back to the user**. In some cases, commands are issued to retrieve some information, for instance the current balance for the group. In other cases no information is provided, but the bot should respond `Done` anyway, to signal that the command was successful. The bot should respond `Done` even in cases when the command is supposed to output some information, but the output is empty (e.g. after issuing a `BALANCE` command before adding any expense).

In case the outcome is unsuccessful, your bot **shouldn't send any message back**. Even in case of wrong insertion, just fail silently, and don't return any error message to the user, so that it's easier for us to test your code.

### Input and output

All numbers passed to your bot (no matter what command they are passed with) have to meet the following requirements. In case they don't, your bot should ignore the command altogether:

- They should always be positive.
- They may be either integers or decimal numbers.
- If they are decimal numbers, they may have at most 2 digits after the decimal point.


When your bot sends numbers back to the user:

- They should always be positive.
- They should be expressed with exactly two decimal digits.

Both in input and output numbers should use `.` &ndash; not `,` &ndash; as decimal separator.

For what concerns your bot's answers:

- They should always be returned in one single message.
- If they contain multiple lines, they should be separated by the character `\n`, with no extra space added.

### Add expense

This is the most important feature, which allows to report a group-related expense. The payer is responsible for adding an expense, which can be done by typing:

```
<AMOUNT>|<HANDLE>[,<HANDLE>]*[ "<MESSAGE>"]
```

Examples:

```
20|MD,LF "Take away"
LQ: 10|GP "Ice cream"
PB: 15|PB,AC
```

The command should start with an amount followed by the separator `|`, followed by the list of people the user paid for (remember that if no handle appears before the command, Ambrogio assumes that the message comes from XY). If the payer is involved in the expense, their handle should be added to the list. It's optional to insert a message that describes the expense.

Note that:

- Participant handles must be sequences of exactly two letters taken from the uppercase latin alphabet. No digits or other symbols are allowed.
- The message, if present, is enclosed in double quotes (`"`). No double quotes are allowed in the message.
- The same participant can't be mentioned more than once.


### History

```
HISTORY
```

This command allows to retrieve the list of expenses in which the user was involved. The command takes no parameters.

The bot should return the expenses for which the user is the payer, or in which someone paid for the user. The list should be in chronological order.

For instance, if these transactions involving XX were inserted:

```
XX: 20|XX,MD "Hot chocolate"
LG: 21.90|LG,XX,SC
GP: 11.6|XX,MD "Beer"
```

the output of the `XX: HISTORY` command would be:

```
01/12/15 Hot chocolate - you get back 10.00
04/12/15 - you pay back 7.30
05/12/15 Beer - you pay back 5.80
```

Each expense should have the following format:

```
DD/MM/YY [<MESSAGE> ]- you [get|pay] back <AMOUNT>
```

where `you pay back` means that someone paid for you, and at some point you'll have to pay that amount back, whereas `you get back` means that you paid for someone, and at some point you'll get that amount back.

### Uneven split

Modifiers allow to enrich shared expenses and make them more flexible. Two (and only these two) modifiers should be supported, namely `+` (plus) and `*` (star).
Plus allows to add a fixed amount to one of the people involved in the payment. Let's assume that RC and TG go to a restaurant together, they both eat a pizza but RC also gets a tiramisù (which costs $5.50). When TG pays he should add the following expense:

```
TG: 19.50|RC+5.50,TG
```

The star modifier allows to set quotas for the participants. The acceptable format for the quota is the same as for the add expense command. Let's assume that PB, MM, and LG go to get some drinks. PB drinks one, MM and LG two. When MM pays he should add the following expense:

```
MM: 60|MM*2,LG*2,PB
```

Combinations of the two modifiers are allowed. Let's take an example: LQ, MM, and FP go wild with tapas, and the bill is on LQ. MM eats two tapas costing $10 each and one costing $12, LQ eats two costing $10 each, and FP (who's on a diet) only has one costing $10. LQ could input this expense this way:

```
LQ: 62|MM+2*3,LQ*2,FP
```

Note that the order in which modifiers are written doesn't matter, so LQ could just as well have written:

```
LQ: 62|MM*3+2,LQ*2,FP
```

Either way, he should get back $32 from MM and $10 from FP.

Some statements involving modifiers aren't valid. For instance, the expression:

```
MD: 50|MD,FP+60
```

describes a practically impossible situation. FP can't possibly have spent $60 more than MD when the overall payment for the transaction was $50.

### Balance

```
BALANCE
```

This command displays the group's financial situation, reported as a set of transactions that should be performed in order to settle all the debts within the group.

The answer is a list of people, the user they owe money to, and the amount they owe. The plugin should always provide the same answer regardless of the person that asks for the balance.

The balance should contain at least one line for each person that owes money:

```
LF owes <HANDLE> <AMOUNT>
LF owes <HANDLE> <AMOUNT>
LG owes <HANDLE> <AMOUNT>
```

This list should be minimized: you should provide a solution that entails the minimum number of transactions (in other words the number of lines should be minimal). Your solution should also minimize the total amount of money transferred between people. Finally, you shouldn't worry about the complexity of your algorithm, as long as it meets these two requirements. We'll never test your code on transactions involving more than 5-6 people.

For example given the following expenses:

```
LQ: 10|LF
LF: 10|GR
```

The correct answer for `BALANCE` is:

```
GR owes LQ 10.00
```

Here's another (more elaborate) example:

```
SC: 16.5|EM,SC,RC
RC: 8.00|EM,GP
```

One correct answer for `BALANCE` is:

```
EM owes SC 9.50
GP owes SC 1.50
GP owes RC 2.50
```

The problem is more complex than it might seem. In particular, note that:

- At times a user might end up owing another user they'd never directly borrowed from. This is fine, just focus on minimizing the number of transactions required to repay all debts.
- In some rare cases there might be more than one solution with minimal number of transactions and minimal total amount to be transferred to repay all debts. Don't worry: any of them will do to us. We'll just check that the number of transactions and total amount to be transferred to repay all debts be minimal.

In fact, the previous example accepts another optimal solution, i.e.:

```
EM owes RC 2.50
EM owes SC 7.00
GP owes SC 4.00
```

There's a potential issue involving the effect that the rounding operation has on the amount owed by people in the group. In particular, consider the situation:

```
MM: 10|LG,RC,PB
```

A bot performing a rounding operation would respond that LG, RC, PB all owe MM $3.33. This way, MM would get back $9.99, when he actually lent $10. To make things simpler, you can assume that the amount inserted will always be divisible by the number of people involved in the transaction. As a consequence, you don't need to handle this problem.

### Group

If you're often sharing expenses with a subset of your colleagues, this feature can come in handy. Once a group is created and some participants are added, it can be used as an alias for its participants.

#### Create a group

```
CREATE <GROUP>
```

The group name should contain a minimum of 3 and a maximum of 12 capital letters. No space, number, or special characters are allowed. If the name is available (no other group exists with the same name) and well formatted, the bot should reply `Done` to acknowledge that the group has been created.

#### Add and remove participants

The `ADD` and `DELETE` commands allow to manage the participants of a given group.

```
ADD <HANDLE> <GROUP>
DELETE <HANDLE> <GROUP>
```

In case of success the bot should reply `Done`. The bot should fail silently when trying to add a handle that's already in the group, or when trying to delete a handle that's not in the group. It should also fail when the handle is not well formatted.

#### Use the group

Groups are aliases for multiple participants. Here's an example of how to use them:

```
CREATE SUSHILOVERS
ADD LQ SUSHILOVERS
ADD FP SUSHILOVERS
ADD MB SUSHILOVERS
132|SUSHILOVERS,MD
```

Remember that a participant can't be mentioned more than once, so the bot should not add the following expense:

```
132|SUSHILOVERS,FP
```

Also, consider the following example:

```
CREATE MOVIEBUFFS
ADD AC MOVIEBUFFS
ADD MB MOVIEBUFFS
```

When adding the following expense:

```
210|SUSHILOVERS,MOVIEBUFFS
```

Your bot should fail silently, because MB belongs both to the SUSHILOVERS and the MOVIEBUFFS groups, and the same person cannot be repeated twice in the same transaction.

Modifiers can be used in combo with groups. When this happens the bot should apply the modifiers to each member of the group. For instance if the following transaction is inserted:

```
LQ: 132|SUSHILOVERS+6,MD*2
```

then the output of `BALANCE` would be:

```
FP owes LQ 28.80
MB owes LQ 28.80
MD owes LQ 45.60
```

## How we evaluate your work

We evaluate your work according to two criteria: robustness and code quality.

To evaluate robustness we run a set of tests to make sure that your plugin responds correctly to our inputs. Make sure you've read this document thoroughly and that your code respects the requirements. We won't test for ambiguous cases not explicitly stated in this document.

To evaluate code quality we assess whether it would be easy for someone else to understand your code, modify an existing feature, or add a new one. Keep this in mind while writing your solution.

Use this GitHub repository to develop and submit your plugin. Remember to remove the folders of all the languages you don't use.

**Important**: At Bending Spoons we do everything we can to make sure we evaluate candidates objectively and remove any potential bias from the process. This includes anonymizing your tests before evaluating them. Please help us by **NOT** including any details in your code that could identify you, such as your name, nickname, or email address.

## Copyright & Non-Disclosure Agreement
**Do not disclose this challenge, or any solution to it, to anyone in any form. All rights are reserved by Bending Spoons S.p.A.**.

Have fun!
