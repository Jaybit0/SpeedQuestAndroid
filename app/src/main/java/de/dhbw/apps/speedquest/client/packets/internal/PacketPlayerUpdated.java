package de.dhbw.apps.speedquest.client.packets.internal;

import java.util.Collection;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.packets.Packet;

@PacketID("internal.playerupdate")
public class PacketPlayerUpdated extends Packet {

    private Collection<UserInfo> allPlayers;
    private Collection<UserInfo> updatedPlayers;

    public PacketPlayerUpdated(Collection<UserInfo> allPlayers, Collection<UserInfo> updatedPlayers) {
        this.allPlayers = allPlayers;
        this.updatedPlayers = updatedPlayers;
    }

    public Collection<UserInfo> getAllPlayers() {
        return allPlayers;
    }
    public Collection<UserInfo> getUpdatedPlayers() {
        return updatedPlayers;
    }

}