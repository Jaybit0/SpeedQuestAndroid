package de.dhbw.apps.speedquest.client;

import android.util.Log;

import java.util.HashMap;

import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.packets.PacketGameStateChange;
import de.dhbw.apps.speedquest.client.packets.PacketInitialize;
import de.dhbw.apps.speedquest.client.packets.PacketPlayerUpdate;
import de.dhbw.apps.speedquest.client.packets.PacketTaskAssign;

public class GameCache {

    private final Object lock = new Object();
    private boolean initialized = false;
    private GameState state = GameState.DISCONNECTED;
    private int round = 0;
    private String self;
    private HashMap<String, UserInfo> users = new HashMap<>();
    private TaskInfo currentTask = null;

    public boolean isInitialized() {
        return initialized;
    }

    void init(PacketInitialize initPacket, SpeedQuestClient client) {
        synchronized (lock) {
            users.clear();

            for (UserInfo info : initPacket.getPlayers()) {
                users.put(info.name, info);
            }

            self = initPacket.getSelf().name;
            initialized = true;
        }

        Log.d("SpeedQuest", "GameCache: Initialized.");
    }

    void updatePlayers(PacketPlayerUpdate playerUpdatePacket, SpeedQuestClient client) {
        synchronized (lock) {
            for (UserInfo user : playerUpdatePacket.getUpdatedPlayers()) {
                users.put(user.name, user);
            }
        }

        Log.d("SpeedQuest", "Gamecache: Players updated.");
    }

    void updateGameState(PacketGameStateChange gameStateChangePacket, SpeedQuestClient client) {
        synchronized (lock) {
            for (UserInfo info : gameStateChangePacket.getUpdatedPlayers()) {
                users.put(info.name, info);
            }

            state = gameStateChangePacket.getNewState();
            round = gameStateChangePacket.getRound();
        }
    }

    void assignTask(PacketTaskAssign taskAssignPacket, SpeedQuestClient client) {
        currentTask = taskAssignPacket.getTask();
    }
}
