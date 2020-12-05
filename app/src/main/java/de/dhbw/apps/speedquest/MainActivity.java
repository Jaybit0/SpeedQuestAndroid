package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.packets.PacketInitialize;

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

    private void tryConnect(String username, String key) {
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.registerPacketHandler(this::handleInitPacket, PacketInitialize.class, this);
        app.client.tryConnect("project-talk.me", 4430, username, key);
    }

    private void handleInitPacket(PacketInitialize initPacket, SpeedQuestClient client) {
        Log.d("SpeedQuest", "Initialize-packet arrived.");
        Log.d("SpeedQuest", "Gamekey: " + initPacket.getGamekey());
        Log.d("SpeedQuest", "Self: " + initPacket.getSelf().name);
    }
}