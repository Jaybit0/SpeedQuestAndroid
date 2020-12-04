package de.dhbw.apps.speedquest.client;

import androidx.appcompat.app.AppCompatActivity;

import de.dhbw.apps.speedquest.client.packets.Packet;

public class PacketHandlerContext<T extends Packet> {

    final PacketHandler<T> handler;
    final AppCompatActivity activity;

    public PacketHandlerContext(PacketHandler<T> handler, AppCompatActivity activity) {
        this.handler = handler;
        this.activity = activity;
    }

}
