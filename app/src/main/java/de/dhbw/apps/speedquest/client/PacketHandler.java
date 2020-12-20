package de.dhbw.apps.speedquest.client;


import de.dhbw.apps.speedquest.client.packets.Packet;

public interface PacketHandler<T extends Packet> {

    void handlePacket(T packet, SpeedQuestClient client);

}
