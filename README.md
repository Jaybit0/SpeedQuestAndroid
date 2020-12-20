# SpeedQuestAndroid

## What is SpeedQuest?

SpeedQuest is a collection of mini-games. It is an online competition between players in a lobby, where players get rated by their performance.

## Architecture

SpeedQuest uses a simple Client-Server architecture. The server assigns tasks and keeps track of each player's score. It enables the client to broadcast custom packets, send information and receive real-time updates. Minigames get rated client-side, whereas the server processes those ratings and translates them into a specific score.

### Server Architecture

The SpeedQuest server is a NodeJS-server that uses WebSocket. The repository can be found [here](https://github.com/Jaybit0/SpeedQuest).

![](images/SpeedQuestServer.png)

### App Architecture

![](images/SpeedQuestClient.png)

## Communication

The main network handling is done with the SpeedQuestClient. Therefore the app communicates via WebSocket and Json-Packets. Packets can dynamically be registered without modifying the actual client.

### Accessing the SpeedQuestClient

The SpeedQuestclient can be accessed from any activity.

```java
SpeedQuestClient client = ((SpeedQuestApplication)getApplication()).client;
```

### Registering new packets

The registration of new packets enables a fast development of new real-time games and other features. To register a new packet, a new java class is required. Make sure, that the specified class is serializable via [Gson:2.8.6](https://github.com/google/gson). Every packet-class needs to extend the abstract class `Packet`

## Dependencies

[AndroidAsync:2.2.1](https://github.com/koush/AndroidAsync)

[Gson:2.8.6](https://github.com/google/gson)
