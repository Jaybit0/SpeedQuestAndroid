package de.dhbw.apps.speedquest.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.packets.internal.PacketPlayerUpdated;

public class PlayerListModel {

    AppCompatActivity activity;
    SpeedQuestClient client;
    PlayerListAdapter adapter;

    public PlayerListModel(PlayerListAdapter.PlayerViewMode viewMode, AppCompatActivity activity, SpeedQuestClient client){
        this.activity = activity;
        this.client = client;

        adapter = new PlayerListAdapter(viewMode);
    }

    public void initialize(){
        client.registerPacketHandler(this::updatePlayers, PacketPlayerUpdated.class, activity);
        adapter.updateList(client.getGameCache().getUsers());
    }

    private void updatePlayers(PacketPlayerUpdated playerUpdatePacket, SpeedQuestClient client) {
        adapter.updateList(playerUpdatePacket.getAllPlayers());
    }

    public PlayerListAdapter getAdapter(){
        return adapter;
    }
}
