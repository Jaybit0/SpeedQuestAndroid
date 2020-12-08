package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.infos.StartInfo;
import de.dhbw.apps.speedquest.client.packets.PacketStartGame;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameStateChanged;
import de.dhbw.apps.speedquest.client.packets.internal.PacketQuit;
import de.dhbw.apps.speedquest.viewmodel.PlayerListAdapter;
import de.dhbw.apps.speedquest.viewmodel.PlayerListModel;

public class LobbyActivity extends AppCompatActivity {

    private boolean disconnectOnStop = true;
    private PlayerListModel listModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();

        listModel = new PlayerListModel(PlayerListAdapter.PlayerViewMode.LobbyScreen, this, app.client);
        RecyclerView listView = findViewById(R.id.lobbyPlayerList);
        listView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        listView.setAdapter(listModel.getAdapter());

        Button buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setVisibility(app.client.getGameCache().getSelf().isHost ? View.VISIBLE : View.INVISIBLE);
        buttonStart.setOnClickListener(v -> startGame());

        Button buttonQuit = findViewById(R.id.buttonQuit);
        buttonQuit.setOnClickListener(v -> quitGame());
    }

    @Override
    protected void onStart() {
        super.onStart();
        disconnectOnStop = true;
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.registerPacketHandler(this::onQuit, PacketQuit.class, this);
        app.client.registerPacketHandler(this::onGameStateChanged, PacketGameStateChanged.class, this);

        listModel.initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.unregisterMappingsOfActivity(this);

        if (disconnectOnStop)
            app.client.disconnect();
    }

    public void startGame() {
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();

        if (!app.client.getGameCache().getSelf().isHost)
            return;

        PacketStartGame startPacket = new PacketStartGame(new StartInfo(3));

        app.client.sendAsync(startPacket);
    }

    public void quitGame() {
        disconnectOnStop = true;
        finish();
    }

    public void onQuit(PacketQuit packet, SpeedQuestClient client) {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void onGameStateChanged(PacketGameStateChanged packet, SpeedQuestClient client) {
        if (packet.getState() == GameState.IN_GAME) {
            Intent i = new Intent(this, IngameActivity.class);
            startActivity(i);
            disconnectOnStop = false;
            finish();
        } else if (packet.getState() == GameState.FINISHED) {
            Intent i = new Intent(this, FinishedActivity.class);
            startActivity(i);
            disconnectOnStop = false;
            finish();
        } else if (packet.getState() == GameState.WAITING) {
            Intent i = new Intent(this, LobbyActivity.class);
            startActivity(i);
            disconnectOnStop = false;
            finish();
        }
    }
}