package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameStateChanged;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskAssigned;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskFinished;
import de.dhbw.apps.speedquest.game.GameHandler;
import de.dhbw.apps.speedquest.game.handlers.CollectItemsHandler;
import de.dhbw.apps.speedquest.game.handlers.ColorTapGameHandler;
import de.dhbw.apps.speedquest.game.handlers.FastTypingHandler;
import de.dhbw.apps.speedquest.game.handlers.FindWordGameHandler;
import de.dhbw.apps.speedquest.game.handlers.DisarmBombGameHandler;
import de.dhbw.apps.speedquest.game.handlers.OpenSafeHandler;
import de.dhbw.apps.speedquest.game.handlers.QuestionGameHandler;
import de.dhbw.apps.speedquest.game.handlers.ScoreScreenHandler;
import de.dhbw.apps.speedquest.game.handlers.TapColorNotWordHandler;
import de.dhbw.apps.speedquest.game.handlers.UnavailbleGameHandler;
import de.dhbw.apps.speedquest.game.handlers.WhacMoleGameHandler;

public class IngameActivity extends AppCompatActivity {

    private boolean disconnectOnStop = true;
    private Map<String, GameHandler> availableHandlers = new HashMap<>();
    private GameHandler activeHandler;
    private ConstraintLayout miniGameContainer;
    private final GameHandler defaultHandler = new UnavailbleGameHandler(this);
    private final GameHandler scoreHandler = new ScoreScreenHandler(this);

    private TextView roundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingame);

        miniGameContainer = findViewById(R.id.containerMiniGame);
        roundTextView = findViewById(R.id.roundText);

        ImageButton buttonQuit = findViewById(R.id.buttonQuitInGame);
        buttonQuit.setOnClickListener(v -> finish());

        addAvailableHandlers();
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

        app.client.registerPacketHandler(this::onTaskAssigned, PacketTaskAssigned.class, this);
        app.client.registerPacketHandler(this::onGameStateChanged, PacketGameStateChanged.class, this);
        app.client.registerPacketHandler(this::onTaskFinished, PacketTaskFinished.class, this);


        if (app.client.getGameCache().getCurrentTask() != null) {
            onTaskAssigned(new PacketTaskAssigned(app.client.getGameCache().getCurrentTask(), app.client.getGameCache().getRound()), app.client);
        } else {
            showScoreHandler(app.client);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.unregisterMappingsOfActivity(this);

        if (disconnectOnStop && isFinishing())
            app.client.disconnect();
    }

    private void onTaskAssigned(PacketTaskAssigned packet, SpeedQuestClient client) {
        if (packet == null || packet.getAssignedTask() == null) {
            showScoreHandler(client);
        } else {
            roundTextView.setText(String.format(getString(R.string.curr_round_text), packet.getRound()));

            GameHandler handler = availableHandlers.getOrDefault(
                    packet.getAssignedTask().getName(),
                    defaultHandler
            );
            inflateHandler(handler, client, packet.getAssignedTask());
        }
    }

    private void showScoreHandler(SpeedQuestClient client) {
        inflateHandler(scoreHandler, client, null);
    }

    private void inflateHandler(GameHandler handler, SpeedQuestClient client, TaskInfo task){
        clearActiveHandlerIfExists(client);

        activeHandler = handler;
        activeHandler.setHandlerID(UUID.randomUUID());
        activeHandler.begin();
        View v = getLayoutInflater().inflate(activeHandler.getGameResource(), miniGameContainer);
        try {
            handler.initialize(v, task);
            handler.registerPacketHandlers();
        } catch (Exception e) {
            Log.e("SpeedQuest", "", e);
        }
    }

    private void clearActiveHandlerIfExists(SpeedQuestClient client){
        if (activeHandler != null) {
            client.unregisterMappingsOfID(activeHandler.getHandlerID());
            try {
                activeHandler.onEnd();
            } catch (Exception e) {
                Log.e("SpeedQuest", "", e);
            }
            miniGameContainer.removeAllViews();
        }
    }

    private void addAvailableHandlers() {
        availableHandlers.put("opensafe", new OpenSafeHandler(this));
        availableHandlers.put("collectitems", new CollectItemsHandler(this));
        availableHandlers.put("colortap", new ColorTapGameHandler(this));
        availableHandlers.put("tapcolornottext", new TapColorNotWordHandler(this));
        availableHandlers.put("whacmole", new WhacMoleGameHandler(this));
        availableHandlers.put("question", new QuestionGameHandler(this));
        availableHandlers.put("findword", new FindWordGameHandler(this));
        availableHandlers.put("disarmbomb", new DisarmBombGameHandler(this));
        availableHandlers.put("fasttyping", new FastTypingHandler(this));
    }

    public void onTaskFinished(PacketTaskFinished packet, SpeedQuestClient client) {
        showScoreHandler(client);
    }

    public void onGameStateChanged(PacketGameStateChanged packet, SpeedQuestClient client) {
        onChangeGameState(packet.getState());
    }

    private boolean onChangeGameState(GameState gs) {
        boolean change = gs == GameState.FINISHED || gs == GameState.WAITING;

        if (!change)
            return false;

        Intent i = new Intent(this, gs == GameState.FINISHED ? FinishedActivity.class : LobbyActivity.class);
        startActivity(i);
        disconnectOnStop = false;
        finish();
        return true;
    }
}