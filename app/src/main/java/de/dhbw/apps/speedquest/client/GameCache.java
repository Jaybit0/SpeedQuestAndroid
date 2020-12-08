package de.dhbw.apps.speedquest.client;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;

import de.dhbw.apps.speedquest.SpeedQuestApplication;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.packets.PacketGameStateChange;
import de.dhbw.apps.speedquest.client.packets.PacketInitialize;
import de.dhbw.apps.speedquest.client.packets.PacketPlayerUpdate;
import de.dhbw.apps.speedquest.client.packets.PacketTaskAssign;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameInitialized;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameStateChanged;
import de.dhbw.apps.speedquest.client.packets.internal.PacketQuit;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskAssigned;

public class GameCache {

    private final Object lock = new Object();

    private SpeedQuestApplication app;
    private boolean initialized = false;
    private GameState state = GameState.DISCONNECTED;
    private int round = 0;
    private String self;
    private HashMap<String, UserInfo> users = new HashMap<>();
    private TaskInfo currentTask = null;

    public GameCache(SpeedQuestApplication app) {
        this.app = app;
    }

    public void register() {
        app.client.registerPacketHandler(this::init, PacketInitialize.class);
        app.client.registerPacketHandler(this::updatePlayers, PacketPlayerUpdate.class);
        app.client.registerPacketHandler(this::updateGameState, PacketGameStateChange.class);
        app.client.registerPacketHandler(this::assignTask, PacketTaskAssign.class);
        app.client.registerPacketHandler(this::onQuit, PacketQuit.class);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public GameState getGameState() {
        return state;
    }

    public UserInfo getSelf() {
        return users.get(self);
    }

    public Collection<UserInfo> getUsers() {
        return users.values();
    }

    public int getRound() {
        return round;
    }

    public TaskInfo getCurrentTask() {
        return currentTask;
    }

    private void init(PacketInitialize initPacket, SpeedQuestClient client) {
        synchronized (lock) {
            users.clear();

            for (UserInfo info : initPacket.getPlayers()) {
                users.put(info.name, info);
            }

            self = initPacket.getSelf().name;
            initialized = true;
            client.callPacketInUITask(new PacketGameInitialized());
            changeGameState(GameState.WAITING, 0);
        }

        Log.d("SpeedQuest", "GameCache: Initialized.");
    }

    private void updatePlayers(PacketPlayerUpdate playerUpdatePacket, SpeedQuestClient client) {
        synchronized (lock) {
            for (UserInfo user : playerUpdatePacket.getUpdatedPlayers()) {
                users.put(user.name, user);
            }
        }

        Log.d("SpeedQuest", "GameCache: Players updated.");
    }

    private void updateGameState(PacketGameStateChange gameStateChangePacket, SpeedQuestClient client) {
        synchronized (lock) {
            for (UserInfo info : gameStateChangePacket.getUpdatedPlayers()) {
                users.put(info.name, info);
            }

            round = gameStateChangePacket.getRound();
            changeGameState(gameStateChangePacket.getNewState(), gameStateChangePacket.getRound());
        }
    }

    private void assignTask(PacketTaskAssign taskAssignPacket, SpeedQuestClient client) {
        currentTask = taskAssignPacket.getTask();
        app.client.callPacketInUITask(new PacketTaskAssigned(currentTask));
    }

    private void onQuit(PacketQuit packet, SpeedQuestClient client) {
        changeGameState(GameState.DISCONNECTED, 0);
        initialized = false;
        self = null;
        users.clear();
        currentTask = null;
        round = 0;
    }

    private void changeGameState(GameState newState, int round) {
        if (state == newState && this.round == round)
            return;

        GameState oldState = state;
        state = newState;
        app.client.callPacketInUITask(new PacketGameStateChanged(oldState, newState, round));
    }
}
