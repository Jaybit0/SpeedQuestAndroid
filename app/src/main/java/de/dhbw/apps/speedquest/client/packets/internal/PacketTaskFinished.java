package de.dhbw.apps.speedquest.client.packets.internal;

import java.util.Collection;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.packets.Packet;

@PacketID("internal.taskfinished")
public class PacketTaskFinished extends Packet {

    private Collection<UserInfo> updatedPlayers;

    public PacketTaskFinished(Collection<UserInfo> updatedPlayers) {
        this.updatedPlayers = updatedPlayers;
    }

    public Collection<UserInfo> getUpdatedPlayers() {
        return updatedPlayers;
    }

}