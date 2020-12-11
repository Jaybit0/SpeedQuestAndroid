package de.dhbw.apps.speedquest.game;

import android.view.View;

import java.util.UUID;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.SpeedQuestApplication;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.client.packets.PacketTaskDone;

public abstract class GameHandler {

    private UUID handlerID;
    private boolean finished = false;

    protected final IngameActivity activity;

    public GameHandler(IngameActivity activity) {
        this.activity = activity;
    }

    public abstract int getGameResource();

    public abstract void initialize(View inflatedView, TaskInfo task);

    public abstract void registerPacketHandlers();

    public abstract void onEnd();

    public final UUID getHandlerID() {
        return handlerID;
    }

    public final void setHandlerID(UUID handlerID) {
        this.handlerID = handlerID;
    }

    public void begin() {
        finished = false;
    }

    public void finish(int rating) {
        if (finished)
            return;

        finished = true;
        SpeedQuestApplication app = (SpeedQuestApplication)activity.getApplication();
        PacketTaskDone packet = new PacketTaskDone();
        packet.rating = rating;
        packet.taskDone = true;
        app.client.sendAsync(packet);
    }

    public boolean isFinished() {
        return finished;
    }
}
