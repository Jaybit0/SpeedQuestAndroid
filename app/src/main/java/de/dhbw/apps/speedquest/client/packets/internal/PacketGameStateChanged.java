package de.dhbw.apps.speedquest.client.packets.internal;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.packets.Packet;

@PacketID("internal.statechanged")
public class PacketGameStateChanged extends Packet {

    private GameState state;

    public PacketGameStateChanged(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

}
