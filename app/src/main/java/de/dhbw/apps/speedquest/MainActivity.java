package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.packets.PacketInitialize;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameInitialized;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = findViewById(R.id.buttonJoin);
        EditText username = findViewById(R.id.editTextUsername);
        EditText gameKey = findViewById(R.id.editTextGameKey);
        b.setOnClickListener(v -> {
            tryConnect(username.getText().toString(), gameKey.getText().toString());
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.registerPacketHandler(this::handleInitPacket, PacketGameInitialized.class, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.unregisterMappingsOfActivity(this);
        Log.d("SpeedQuest", "Stopped main activity.");
    }

    private void tryConnect(String username, String key) {
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.tryConnect("wss", "project-talk.me", 4430, username, key);
    }

    private void handleInitPacket(PacketGameInitialized initPacket, SpeedQuestClient client) {
        Log.d("SpeedQuest", "Initialize-packet arrived.");

        Intent i = new Intent(this, LobbyActivity.class);
        startActivity(i);
    }
}