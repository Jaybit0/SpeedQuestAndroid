package de.dhbw.apps.speedquest.game.handlers;

import android.view.View;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class ColorTapGameHandler extends GameHandler {

    public ColorTapGameHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_colortap;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {

    }
}
