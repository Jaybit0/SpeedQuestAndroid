package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

public abstract class Packet {

    @SerializedName("packet")
    private String packetID;

    public String getPacketID() {
        return packetID;
    }

}
