package de.dhbw.apps.speedquest.client.packets.internal;

import java.util.Collection;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.UserIngameInfo;
import de.dhbw.apps.speedquest.client.packets.Packet;

@PacketID("internal.taskfinished")
public class PacketTaskFinished extends Packet {

    private Collection<UserIngameInfo> updatedPlayers;

    public PacketTaskFinished(Collection<UserIngameInfo> updatedPlayers) {
        this.updatedPlayers = updatedPlayers;
    }

    public Collection<UserIngameInfo> getUpdatedPlayers() {
        return updatedPlayers;
    }

}