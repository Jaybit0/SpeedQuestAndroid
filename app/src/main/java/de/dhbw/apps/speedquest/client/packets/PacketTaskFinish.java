package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.UserInfo;

@PacketID("taskfinish")
public class PacketTaskFinish extends Packet {

    PacketTaskFinish(){
        packetID = "taskfinish";
    }

    @SerializedName("roundscores")
    private UserInfo[] roundscores;

    public UserInfo[] getRoundscores() {
        return roundscores;
    }

}