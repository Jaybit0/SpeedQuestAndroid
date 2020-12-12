package de.dhbw.apps.speedquest.game.handlers;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class DisarmBombGameHandler extends GameHandler {

    private TextView timerText;
    private ImageView boomImage;
    private ImageView boomSmokeImage;
    private ImageView bombImage;
    private ImageView timerImage;
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
        boomImage = inflatedView.findViewById(R.id.imageBoom);
        boomSmokeImage = inflatedView.findViewById(R.id.imageBoomSmoke);
        bombImage = inflatedView.findViewById(R.id.imageBomb);
        timerImage = inflatedView.findViewById(R.id.imageTimer);

        boomImage.setVisibility(View.INVISIBLE);
        boomSmokeImage.setVisibility(View.INVISIBLE);

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

        if (delta < 0) {
            boomImage.setVisibility(View.VISIBLE);
            boomSmokeImage.setVisibility(View.VISIBLE);
            timerText.setVisibility(View.INVISIBLE);
            timerImage.setVisibility(View.INVISIBLE);
            bombImage.setVisibility(View.INVISIBLE);
            handler.postDelayed(() -> finish(0), 1000);
            return;
        }

        timerText.setText(String.format("%.1f", delta / 1000f));
        timerText.setAlpha(Math.max((3000 - System.currentTimeMillis() + startMillis) / 3000f, 0));
        handler.postDelayed(timerUpdater, 100);
    }

    private void onClickBomb() {
        if (isFinished())
            return;

        long delta = targetMillis - System.currentTimeMillis();

        if (delta < 0)
            return;

        timerText.setAlpha(1);
        finish(delta > 0 ? (int) Math.max((5000 - delta) / 50, 0) : 0);
    }
}
