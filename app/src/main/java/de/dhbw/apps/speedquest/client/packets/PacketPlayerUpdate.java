package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.UserInfo;

@PacketID("playerupdate")
public class PacketPlayerUpdate extends Packet {

    @SerializedName("updateplayers")
    private UserInfo[] updatedPlayers;

    public UserInfo[] getUpdatedPlayers() {
        return updatedPlayers;
    }

}
