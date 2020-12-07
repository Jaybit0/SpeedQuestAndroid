package de.dhbw.apps.speedquest.game;

import java.util.UUID;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;

public abstract class GameHandler {

    private UUID handlerID;

    protected final IngameActivity activity;

    public GameHandler(IngameActivity activity) {
        this.activity = activity;
    }

    public abstract int getGameResource();

    public abstract void initialize(TaskInfo task);

    public abstract void registerPacketHandlers();

    public abstract void onEnd();

    public final UUID getHandlerID() {
        return handlerID;
    }

    public final void setHandlerID(UUID handlerID) {
        this.handlerID = handlerID;
    }

}
