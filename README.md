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

The registration of new packets enables a fast development of new real-time games and other features. To register a new packet, a new java class is required. Make sure, that the specified class is serializable via [Gson:2.8.6](https://github.com/google/gson). Every packet-class needs to extend the abstract class `Packet`. Every packet is identified by an unique name. The name needs to be specified with a custom annotation `PacketID`. To be able to send the packet, the protected String `packetID` needs to be set in the constructor.

```java
// To be able to identify the incoming packet
@PacketID("example")
public class PacketExample extends Packet {

    @SerializedName("var1")
    private String var1;

    public PacketExample() {
        // To be able to send the packet correctly
        super.packetID = "example";
    }
}
```

The packet is now ready to be registered. This will tell the SpeedQuestClient how to deserialize an incoming json with the id `example`.

```java
client.registerPacket(PacketExample.class);
```

Now, the SpeedQuestClient is able to deserialize the new packet. To evaluate the incoming example-packets, a `PacketHandler` is needed.

## Dependencies

[AndroidAsync:2.2.1](https://github.com/koush/AndroidAsync)

[Gson:2.8.6](https://github.com/google/gson)
