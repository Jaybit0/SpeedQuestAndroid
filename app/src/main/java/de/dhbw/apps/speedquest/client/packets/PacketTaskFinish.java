package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.infos.UserIngameInfo;

@PacketID("taskfinish")
public class PacketTaskFinish extends Packet {

    @SerializedName("roundscores")
    private UserIngameInfo[] roundscores;

    PacketTaskFinish(){
        packetID = "taskfinish";
    }

    public UserIngameInfo[] getRoundscores() {
        return roundscores;
    }

}