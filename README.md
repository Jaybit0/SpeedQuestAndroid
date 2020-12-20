# SpeedQuestAndroid

## What is SpeedQuest?

SpeedQuest is a collection of mini-games. It is an online competition between players in a lobby, where players get rated by their performance.

## Architecture

SpeedQuest uses a simple Server-Client architecture. The server assigns tasks and keeps track of each player's score. It enables the client to broadcast custom packets and receive real-time updates. Minigames get rated client-side, whereas the server processes those ratings and translates them into a specific score.
