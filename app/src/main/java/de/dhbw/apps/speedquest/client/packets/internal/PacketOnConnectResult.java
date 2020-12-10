package de.dhbw.apps.speedquest.client.packets.internal;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.packets.Packet;

@PacketID("internal.onconnectresult")
public class PacketOnConnectResult extends Packet {

    public Exception ex;

    public PacketOnConnectResult(Exception ex) {
        this.ex = ex;
    }

}
