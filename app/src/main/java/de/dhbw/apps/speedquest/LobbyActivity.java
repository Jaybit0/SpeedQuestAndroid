package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
    private int rounds;
    private EditText roundCount;

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

        ImageButton buttonQuit = findViewById(R.id.imageButtonQuit);
        buttonQuit.setOnClickListener(v -> quitGame());

        ImageButton shareButton = findViewById(R.id.sharebutton);
        shareButton.setOnClickListener(this::shareGame);

        rounds=3;
        roundCount = (EditText) findViewById(R.id.editTextRounds);
        roundCount.setVisibility(app.client.getGameCache().getSelf().isHost ? View.VISIBLE : View.INVISIBLE);
        roundCount.setText("3");
        roundCount.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                changeRounds(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        EditText gameKey = (EditText) findViewById(R.id.ediTextLobbyKey);
        String key = app.client.getGameCache().getGameKey();
        gameKey.setText(key);
    }

    @Override
    protected void onStart() {
        super.onStart();
        disconnectOnStop = true;
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();

        if (!app.client.isConnected()) {
            finish();
            return;
        }

        if (onChangeGameState(app.client.getGameCache().getGameState()))
            return;

        app.client.registerPacketHandler(this::onQuit, PacketQuit.class, this);
        app.client.registerPacketHandler(this::onGameStateChanged, PacketGameStateChanged.class, this);

        listModel.initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.unregisterMappingsOfActivity(this);

        if (disconnectOnStop && isFinishing())
            app.client.disconnect();
    }

    public void startGame() {
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();

        if(rounds < 1 || rounds > 99) {
            rounds = 3;
            roundCount.setText("3");
            Toast.makeText(getApplicationContext(),"Invalid round count (0-99)",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!app.client.getGameCache().getSelf().isHost)
            return;

        PacketStartGame startPacket = new PacketStartGame(new StartInfo(rounds));

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
        onChangeGameState(packet.getState());
    }

    private boolean onChangeGameState(GameState gs) {
        boolean change = gs == GameState.FINISHED || gs == GameState.IN_GAME;

        if (!change)
            return false;

        Intent i = new Intent(this, gs == GameState.FINISHED ? FinishedActivity.class : IngameActivity.class);
        startActivity(i);
        disconnectOnStop = false;
        finish();
        return true;
    }

    private void changeRounds(String newRounds){
        try {
            rounds = Integer.parseInt(newRounds);
        } catch (Exception e) {
            Log.d("SpeedQuest", "Could not parse new rounds.");
        }
    }

    private void shareGame(View v){
        SpeedQuestApplication app = (SpeedQuestApplication) getApplication();
        String link = "https://project-talk.me:4430/gamelink?key="
                + app.client.getGameCache().getGameKey();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Share Gamelink");
        i.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(i, "Share link:"));
    }
}