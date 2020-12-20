package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameInitialized;
import de.dhbw.apps.speedquest.client.packets.internal.PacketOnConnectResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.buttonJoin);
        EditText username = findViewById(R.id.editTextUsername);
        EditText gameKey = findViewById(R.id.editTextGameKey);
        b.setOnClickListener(v -> tryConnect(username.getText().toString(), gameKey.getText().toString()));

        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data != null && Objects.requireNonNull(data.getQueryParameter("key")).length() == 4)
            gameKey.setText(data.getQueryParameter("key"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.registerPacketHandler(this::handleInitPacket, PacketGameInitialized.class, this);
        app.client.registerPacketHandler(this::handleOnConnectResult, PacketOnConnectResult.class, this);
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

    private void handleOnConnectResult(PacketOnConnectResult packet, SpeedQuestClient client) {
        if (packet.ex != null)
            Toast.makeText(this, "Could not connect. Make sure you have access to the internet.", Toast.LENGTH_SHORT).show();
    }
}