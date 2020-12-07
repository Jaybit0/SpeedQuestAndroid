package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameStateChanged;
import de.dhbw.apps.speedquest.client.packets.internal.PacketQuit;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskAssigned;

public class FinishedActivity extends AppCompatActivity {

    private boolean disconnectOnStop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);
    }

    @Override
    protected void onStart() {
        super.onStart();
        disconnectOnStop = true;
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.registerPacketHandler(this::onGameStateChanged, PacketGameStateChanged.class, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.unregisterMappingsOfActivity(this);

        if (disconnectOnStop)
            app.client.disconnect();
    }

    public void onGameStateChanged(PacketGameStateChanged packet, SpeedQuestClient client) {
        if (packet.getNewState() == GameState.IN_GAME) {
            Intent i = new Intent(this, IngameActivity.class);
            startActivity(i);
            disconnectOnStop = false;
            finish();
        } else if (packet.getNewState() == GameState.FINISHED) {
            Intent i = new Intent(this, FinishedActivity.class);
            startActivity(i);
            disconnectOnStop = false;
            finish();
        } else if (packet.getNewState() == GameState.WAITING) {
            Intent i = new Intent(this, LobbyActivity.class);
            startActivity(i);
            disconnectOnStop = false;
            finish();
        }
    }
}