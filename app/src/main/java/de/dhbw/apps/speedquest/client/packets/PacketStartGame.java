package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.StartInfo;

@PacketID("startgame")
public class PacketStartGame extends Packet {

    @SerializedName("settings")
    private StartInfo settings;

    public PacketStartGame() {
        packetID = "startgame";
    }

    public PacketStartGame(StartInfo settings) {
        this.settings = settings;
        this.packetID = "startgame";
    }

    public StartInfo getSettings() {
        return settings;
    }
}
