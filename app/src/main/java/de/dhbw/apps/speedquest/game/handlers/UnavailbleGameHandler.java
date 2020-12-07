package de.dhbw.apps.speedquest.game.handlers;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class UnavailbleGameHandler extends GameHandler {


    public UnavailbleGameHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_unknown;
    }

    @Override
    public void initialize(TaskInfo info) {
    }

    @Override
    public void registerPacketHandlers() {
    }

    @Override
    public void onEnd() {
    }
}
