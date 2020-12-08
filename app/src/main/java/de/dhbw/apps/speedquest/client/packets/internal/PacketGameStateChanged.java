package de.dhbw.apps.speedquest.client.packets.internal;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.packets.Packet;

@PacketID("internal.statechanged")
public class PacketGameStateChanged extends Packet {

    private GameState oldState;
    private GameState newState;
    private int newRound;

    public PacketGameStateChanged(GameState oldState, GameState newState, int newRound) {
        this.oldState = oldState;
        this.newState = newState;
        this.newRound = newRound;
    }

    public GameState getOldState() {
        return oldState;
    }

    public GameState getNewState() {
        return newState;
    }

    public int getNewRound() { return newRound; };

    public boolean stateChanged() { return oldState != newState; }

}
