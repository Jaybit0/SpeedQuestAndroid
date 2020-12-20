package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.UserInfo;

@PacketID("gamestate")
public class PacketGameStateChange extends Packet {

    @SerializedName("gamestate")
    private String newState;

    @SerializedName("roundcount")
    private int roundCount;

    public PacketGameStateChange() {
        packetID = "gamestate";
    }

    public GameState getNewState() {
        switch (newState) {
            case "WAITING":
                return GameState.WAITING;
            case "INGAME":
                return GameState.IN_GAME;
            case "FINISHED":
                return GameState.FINISHED;
            default:
                return null;
        }
    }

    public int getRoundCount() {
        return roundCount;
    }
}
