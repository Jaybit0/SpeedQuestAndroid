package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.dhbw.apps.speedquest.client.GameState;
import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameStateChanged;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskAssigned;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskFinished;
import de.dhbw.apps.speedquest.game.GameHandler;
import de.dhbw.apps.speedquest.game.handlers.ColorTapGameHandler;
import de.dhbw.apps.speedquest.game.handlers.ScoreScreenHandler;
import de.dhbw.apps.speedquest.game.handlers.UnavailbleGameHandler;

public class IngameActivity extends AppCompatActivity {

    private boolean disconnectOnStop = true;
    private Map<String, GameHandler> availableHandlers = new HashMap<>();
    private GameHandler activeHandler;
    private ConstraintLayout miniGameContainer;
    private final GameHandler defaultHandler = new UnavailbleGameHandler(this);
    private final GameHandler scoreHandler = new ScoreScreenHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingame);

        miniGameContainer = findViewById(R.id.containerMiniGame);

        ImageButton buttonQuit = findViewById(R.id.buttonQuitInGame);
        buttonQuit.setOnClickListener(v -> finish());

        addAvailableHandlers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        disconnectOnStop = true;
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.registerPacketHandler(this::onTaskAssigned, PacketTaskAssigned.class, this);
        app.client.registerPacketHandler(this::onGameStateChanged, PacketGameStateChanged.class, this);
        app.client.registerPacketHandler(this::onTaskFinished, PacketTaskFinished.class, this);

        showDefaultHandler(app.client);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.unregisterMappingsOfActivity(this);

        if (disconnectOnStop)
            app.client.disconnect();
    }

    private void onTaskAssigned(PacketTaskAssigned packet, SpeedQuestClient client) {
        if (packet == null || packet.getAssignedTask() == null) {
            showDefaultHandler(client);
        } else {
            clearActiveHandlerIfExists(client);

            activeHandler = availableHandlers.getOrDefault(packet.getAssignedTask().getName(), defaultHandler);
            activeHandler.setHandlerID(UUID.randomUUID());
            View v = getLayoutInflater().inflate(activeHandler.getGameResource(), miniGameContainer);
            try {
                activeHandler.initialize(v, packet.getAssignedTask());
                activeHandler.registerPacketHandlers();
            } catch (Exception e) {
                Log.e("SpeedQuest", "", e);
            }
        }
    }

    private void showDefaultHandler(SpeedQuestClient client) {
        clearActiveHandlerIfExists(client);

        activeHandler = scoreHandler;
        activeHandler.setHandlerID(UUID.randomUUID());
        View v = getLayoutInflater().inflate(activeHandler.getGameResource(), miniGameContainer);
        activeHandler.initialize(v, null);
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
        availableHandlers.put("colortap", new ColorTapGameHandler(this));
    }

    public void onTaskFinished(PacketTaskFinished packet, SpeedQuestClient client) {
        showDefaultHandler(client);
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