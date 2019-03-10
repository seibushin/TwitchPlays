# TwitchPlays
TwitchPlays is a chatbot that aims to up the interaction with the community.
It is distributed under the same SeiBot.

The individual features of SeiBot need to be added to your obs scene to be displayed. Simply as a window capture.

![Image of SeiBot](https://github.com/seibushin/TwitchPlays/blob/master/img/seibot.png)

## Features
### PointBot
The PointBot distributes points to the users in your chat. You can set the amount and the frequently.

### oMeter
The oMeter uses a set of positive and negative words to determine if the chat message was positive. If it was possible the oMeter will show the mood of your chat accordingly. You can configure how sensible the oMeter should react to messages.

![Image of oMeter](https://github.com/seibushin/TwitchPlays/blob/master/img/oMeter.png)

### SoundBot
The SoundBot allows you to configure sounds and their cost to available to your users. For example we could put the following content into the sounds.txt file:
`hi;1;Hi. OK!`
If we now have the MP3 `hi.mp3` in the sounds dir a user with at least 1 points from the point bot can use the command `!sound hi` to play the sound and display the Message `Hi. OK!`

![Image of SoundBot](https://github.com/seibushin/TwitchPlays/blob/master/img/soundBot.png)

### ApmBot
The ApmBot allows you to display your APM (actions per minute) by displaying a keyboard and a mouse. You con configure it to only display the mouse or keyboard or only the APM.

![Image of ApmBot](https://github.com/seibushin/TwitchPlays/blob/master/img/ApmBot.png)

### LoLGame
LoLGame allows your community to vote for a role and champ you should play. By sending a message with `!pick mid` the community can vote for you to play midlane. If you are ready to select a champ you simply click the lock symbol and wait for your community to decide what champ to play.

![Image of LoLGame Pick](https://github.com/seibushin/TwitchPlays/blob/master/img/LoLGame_pick.png)

The community can now participate in the vote for your champ by typing `!pick annie` for example. The View will show the splash art of the most voted champ and additionally the next 5 in the ranking.

![Image of LoLGame Pick](https://github.com/seibushin/TwitchPlays/blob/master/img/LoLGame_champ.png)

## Future Work

### Features in development or experimental features
#### Press for me
As demonstrated by the TwitchPlaysPokemon streams it can be a lot of letting twitch do its thing and play for you. Similar to this idea, press for me allows the twitchchat to do some key inputs for you. But since there is a lot of delay this is only experimental and not available in any released version. Check the sourcecode for more information.
### PokeFight
(not fully implemented - check sourcecode)
PokeFight allows the community to play against each other. The idea is, that every user get a pokemon which he can train. The fight is actually done by sending your commands to and your pokemon then execudes your commands for example you could expect your pokemon to have more agility and therefore is able to attack first, so you want to start with an attack aferwards you expect your opponent to attack you which leads to use block as a second input. A simple prototype is already implemented but highly experimental.

## Future Work
For future releases it is planned to use a web interface which will make a lot of the features simpler to display inside of OBS.

## Current Status
Currently no development is done!
