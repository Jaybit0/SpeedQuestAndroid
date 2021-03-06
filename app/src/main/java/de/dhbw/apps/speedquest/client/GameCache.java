package de.dhbw.apps.speedquest.client;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.dhbw.apps.speedquest.SpeedQuestApplication;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.infos.UserIngameInfo;
import de.dhbw.apps.speedquest.client.packets.PacketGameStateChange;
import de.dhbw.apps.speedquest.client.packets.PacketInitialize;
import de.dhbw.apps.speedquest.client.packets.PacketPlayerUpdate;
import de.dhbw.apps.speedquest.client.packets.PacketTaskAssign;
import de.dhbw.apps.speedquest.client.packets.PacketTaskFinish;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameInitialized;
import de.dhbw.apps.speedquest.client.packets.internal.PacketGameStateChanged;
import de.dhbw.apps.speedquest.client.packets.internal.PacketPlayerUpdated;
import de.dhbw.apps.speedquest.client.packets.internal.PacketQuit;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskAssigned;
import de.dhbw.apps.speedquest.client.packets.internal.PacketTaskFinished;

public class GameCache {

    private final Object lock = new Object();

    private SpeedQuestApplication app;
    private boolean initialized = false;
    private GameState state = GameState.DISCONNECTED;
    private int round = 0;
    private int roundCount = 3;
    private String self;
    private HashMap<String, UserInfo> users = new HashMap<>();
    private List<UserIngameInfo> lastScores = new ArrayList<>();
    private TaskInfo currentTask = null;
    private String gameKey;

    public GameCache(SpeedQuestApplication app) {
        this.app = app;
    }

    public void register() {
        app.client.registerPacketHandler(this::init, PacketInitialize.class);
        app.client.registerPacketHandler(this::updatePlayers, PacketPlayerUpdate.class);
        app.client.registerPacketHandler(this::updateGameState, PacketGameStateChange.class);
        app.client.registerPacketHandler(this::assignTask, PacketTaskAssign.class);
        app.client.registerPacketHandler(this::finishTask, PacketTaskFinish.class);
        app.client.registerPacketHandler(this::onQuit, PacketQuit.class);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public GameState getGameState() {
        return state;
    }

    public UserInfo getSelf() {
        return users.getOrDefault(self, UserInfo.defaultUserSelf);
    }

    public String getGameKey() { return gameKey; }

    public Collection<UserInfo> getUsers() {
        return users.values();
    }

    public List<UserIngameInfo> getLastUserScores() {
        return lastScores;
    }

    public int getRound() {
        return round;
    }

    public TaskInfo getCurrentTask() {
        return currentTask;
    }

    public int getRoundCount() {
        return roundCount;
    }

    private void init(PacketInitialize initPacket, SpeedQuestClient client) {
        synchronized (lock) {
            users.clear();

            for (UserInfo info : initPacket.getPlayers()) {
                users.put(info.name, info);
            }

            self = initPacket.getSelf().name;
            gameKey = initPacket.getGamekey();
            initialized = true;
            client.callPacketInUITask(new PacketGameInitialized());
            changeGameState(GameState.WAITING);
        }

        Log.d("SpeedQuest", "GameCache: Initialized.");
    }

    private void updatePlayers(PacketPlayerUpdate playerUpdatePacket, SpeedQuestClient client) {
        synchronized (lock) {
            for (UserInfo user : playerUpdatePacket.getUpdatedPlayers()) {
                users.put(user.name, user);
            }
            client.callPacketInUITask(new PacketPlayerUpdated(users.values(), Arrays.asList(playerUpdatePacket.getUpdatedPlayers())));
        }

        Log.d("SpeedQuest", "GameCache: Players updated.");
    }

    private void updateGameState(PacketGameStateChange gameStateChangePacket, SpeedQuestClient client) {
        roundCount = gameStateChangePacket.getRoundCount();
        synchronized (lock) {
            changeGameState(gameStateChangePacket.getNewState());
        }
    }

    private void assignTask(PacketTaskAssign taskAssignPacket, SpeedQuestClient client) {
        currentTask = taskAssignPacket.getTask();
        round = taskAssignPacket.getRound();
        app.client.callPacketInUITask(new PacketTaskAssigned(currentTask, round));
    }

    private void finishTask(PacketTaskFinish finishTaskPacket, SpeedQuestClient client) {
        synchronized (lock) {
            for (UserInfo info : finishTaskPacket.getRoundscores()) {
                users.put(info.name, info);
            }
            lastScores.clear();
            lastScores.addAll(Arrays.asList(finishTaskPacket.getRoundscores()));
            client.callPacketInUITask(new PacketTaskFinished(lastScores));
            currentTask = null;
        }
    }

    private void onQuit(PacketQuit packet, SpeedQuestClient client) {
        changeGameState(GameState.DISCONNECTED);
        initialized = false;
        self = null;
        users.clear();
        currentTask = null;
        round = 0;
    }

    private void changeGameState(GameState newState) {
        lastScores.clear();
        state = newState;
        app.client.callPacketInUITask(new PacketGameStateChanged(state));
    }
}
