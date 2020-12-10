package de.dhbw.apps.speedquest.game.handlers;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class DisarmBombGameHandler extends GameHandler {

    private TextView timerText;
    private Handler handler;
    private long startMillis;
    private long targetMillis;
    private Runnable timerUpdater;

    public DisarmBombGameHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_disarmthebomb;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        handler = new Handler();

        inflatedView.findViewById(R.id.imageBomb).setOnClickListener(v -> onClickBomb());
        timerText = inflatedView.findViewById(R.id.timerText);

        int initialSeconds = (int)((double)task.getParam("seconds", 20.0));
        startMillis = System.currentTimeMillis();
        targetMillis = startMillis + initialSeconds * 1000;
        timerUpdater = this::updateText;

        updateText();
        handler.postDelayed(timerUpdater, 100);
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {
        handler.removeCallbacks(timerUpdater);
    }

    private void updateText() {
        if (isFinished())
            return;

        long delta = targetMillis - System.currentTimeMillis();
        timerText.setText(String.format("%.1f", delta / 1000f));
        timerText.setAlpha(Math.max((3000 - System.currentTimeMillis() + startMillis) / 3000f, 0));
        handler.postDelayed(timerUpdater, 100);
    }

    private void onClickBomb() {
        if (isFinished())
            return;

        long delta = targetMillis - System.currentTimeMillis();
        timerText.setAlpha(1);
        finish(delta > 0 ? (int) delta / 50 : 0);
    }
}
