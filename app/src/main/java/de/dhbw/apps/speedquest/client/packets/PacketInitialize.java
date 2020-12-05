package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.UserInfo;

@PacketID("initial")
public class PacketInitialize extends Packet {

    @SerializedName("gamekey")
    private String gamekey;

    @SerializedName("userself")
    private UserInfo self;

    @SerializedName("playerlist")
    private UserInfo[] players;

    public String getGamekey() {
        return gamekey;
    }

    public UserInfo getSelf() {
        return self;
    }

    public UserInfo[] getPlayers() {
        return players;
    }

}
