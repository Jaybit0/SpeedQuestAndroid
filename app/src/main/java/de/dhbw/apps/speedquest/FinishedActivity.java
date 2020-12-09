package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.infos.StartInfo;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.packets.PacketStartGame;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameStateChanged;
import de.dhbw.apps.speedquest.client.packets.internal.PacketQuit;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskAssigned;
import de.dhbw.apps.speedquest.viewmodel.PlayerListAdapter;
import de.dhbw.apps.speedquest.viewmodel.PlayerListModel;

public class FinishedActivity extends AppCompatActivity {

    private boolean disconnectOnStop = true;
    private PlayerListModel listModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);

        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();

        listModel = new PlayerListModel(PlayerListAdapter.PlayerViewMode.FinishScreen, this, app.client);
        RecyclerView listView = findViewById(R.id.finishPlayerList);
        listView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        listView.setAdapter(listModel.getAdapter());

        ImageButton buttonQuit = findViewById(R.id.buttonQuitFinish);
        buttonQuit.setOnClickListener(v -> finish());

        Button buttonStart = findViewById(R.id.buttonRestart);
        buttonStart.setVisibility(app.client.getGameCache().getSelf().isHost ? View.VISIBLE : View.INVISIBLE);
        buttonStart.setOnClickListener(v -> startGame());

        ArrayList<UserInfo> users = new ArrayList<UserInfo>(app.client.getGameCache().getUsers());
        users.sort((i1, i2) -> i2.score - i1.score);

        TextView firstText = findViewById(R.id.textFirst);
        TextView secondText = findViewById(R.id.textSecond);
        TextView thirdText = findViewById(R.id.textThird);

        firstText.setText(users.size() < 1 ? "" : users.get(0).name);
        firstText.setTextColor(users.size() < 1 ? Color.WHITE : Color.parseColor(users.get(0).color));
        secondText.setText(users.size() < 2 ? "" : users.get(1).name);
        secondText.setTextColor(users.size() < 2 ? Color.WHITE : Color.parseColor(users.get(1).color));
        thirdText.setText(users.size() < 3 ? "" : users.get(2).name);
        thirdText.setTextColor(users.size() < 3 ? Color.WHITE : Color.parseColor(users.get(2).color));
    }

    @Override
    protected void onStart() {
        super.onStart();
        disconnectOnStop = true;
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
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