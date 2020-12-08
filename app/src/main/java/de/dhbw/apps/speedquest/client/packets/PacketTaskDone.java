package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import de.dhbw.apps.speedquest.client.PacketID;

@PacketID("taskinfo")
public class PacketTaskDone extends Packet {

    @SerializedName("taskdone")
    public boolean taskDone;

    @SerializedName("rating")
    public int rating;

    public PacketTaskDone() {
        packetID = "taskinfo";
    }

}
