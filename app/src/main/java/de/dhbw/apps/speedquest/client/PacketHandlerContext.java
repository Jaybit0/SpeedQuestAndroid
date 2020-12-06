package de.dhbw.apps.speedquest.client;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

import de.dhbw.apps.speedquest.client.packets.Packet;

public class PacketHandlerContext<T extends Packet> {

    final UUID id;
    final PacketHandler<T> handler;
    final AppCompatActivity activity;

    public PacketHandlerContext(PacketHandler<T> handler, AppCompatActivity activity) {
        this(UUID.randomUUID(), handler, activity);
    }

    public PacketHandlerContext(UUID id, PacketHandler<T> handler, AppCompatActivity activity) {
        this.id = id;
        this.handler = handler;
        this.activity = activity;
    }

}
