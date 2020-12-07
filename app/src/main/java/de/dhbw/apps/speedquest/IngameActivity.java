package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskAssigned;
import de.dhbw.apps.speedquest.game.GameHandler;
import de.dhbw.apps.speedquest.game.handlers.UnavailbleGameHandler;

public class IngameActivity extends AppCompatActivity {

    private Map<String, GameHandler> availableHandlers = new HashMap<>();
    private GameHandler activeHandler;
    private ConstraintLayout miniGameContainer;
    private final GameHandler defaultHandler = new UnavailbleGameHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingame);

        miniGameContainer = findViewById(R.id.containerMiniGame);

        Button buttonQuit = findViewById(R.id.buttonQuitInGame);
        buttonQuit.setOnClickListener(v -> finish());

        addAvailableHandlers();
        Log.d("SpeedQuest", "Created Ingame-object");
    }

    @Override
    protected void onStart() {
        super.onStart();
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.registerPacketHandler(this::onTaskAssigned, PacketTaskAssigned.class, this);

        onTaskAssigned(new PacketTaskAssigned(app.client.getGameCache().getCurrentTask()), app.client);
    }

    @Override
    protected void onStop() {
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.disconnect();
        app.client.unregisterMappingsOfActivity(this);
        super.onStop();
    }

    private void onTaskAssigned(PacketTaskAssigned packet, SpeedQuestClient client) {
        if (activeHandler != null) {
                client.unregisterMappingsOfID(activeHandler.getHandlerID());
                activeHandler.onEnd();
                miniGameContainer.removeAllViews();
        }

        activeHandler = packet.getAssignedTask() == null ? defaultHandler : availableHandlers.getOrDefault(packet.getAssignedTask().getName(), defaultHandler);
        activeHandler.setHandlerID(UUID.randomUUID());
        getLayoutInflater().inflate(activeHandler.getGameResource(), miniGameContainer);
        activeHandler.initialize(packet.getAssignedTask());
        activeHandler.registerPacketHandlers();
    }

    private void addAvailableHandlers() {

    }
}