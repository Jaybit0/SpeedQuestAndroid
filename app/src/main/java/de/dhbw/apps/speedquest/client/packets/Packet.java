package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

public abstract class Packet {

    @SerializedName("packet")
    public final String packetID;

    public Packet(String packetID) {
        this.packetID = packetID;
    }

}
