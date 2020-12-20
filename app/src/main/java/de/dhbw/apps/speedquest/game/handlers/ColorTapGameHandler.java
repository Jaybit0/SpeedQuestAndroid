package de.dhbw.apps.speedquest.game.handlers;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class ColorTapGameHandler extends GameHandler {

    private Random rd;
    private Handler handler;
    private CardView cv;
    private Runnable updater = this::onUpdateColor;
    private Integer targetColor;
    private Integer currentColor;
    private List<Integer> colorList = new ArrayList<>();
    private long startMillis;
    private int failedClicks;
    private TextView failedText;

    public ColorTapGameHandler(IngameActivity activity) {
        super(activity);
        colorList.add(Color.RED);
        colorList.add(0xffffa500); // Orange
        colorList.add(Color.YELLOW);
        colorList.add(Color.GREEN);
        colorList.add(Color.CYAN);
        colorList.add(Color.BLUE);
        colorList.add(Color.MAGENTA);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_colortap;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        cv = inflatedView.findViewById(R.id.cardViewColorTap);
        cv.setOnClickListener(v -> onClick());

        try {
            double seedDouble = task.getParam("seed", new Double(new Random().nextInt()));
            rd = new Random((int)seedDouble);
            String colorName = task.getParam("color", "red");

            switch (colorName) {
                case "orange":
                    targetColor = 0xffffa500;
                    break;
                case "yellow":
                    targetColor = Color.YELLOW;
                    break;
                case "green":
                    targetColor = Color.GREEN;
                    break;
                case "cyan":
                    targetColor = Color.CYAN;
                    break;
                case "blue":
                    targetColor = Color.BLUE;
                    break;
                case "magenta":
                    targetColor = Color.MAGENTA;
                    break;
                default:
                    targetColor = Color.RED;
            }

            CardView cv = inflatedView.findViewById(R.id.led_card);
            cv.setCardBackgroundColor(targetColor);
            failedText = inflatedView.findViewById(R.id.textTaskColorTapFailedCount);
        } catch (Exception e) {
            Log.e("SpeedQuest", "", e);
            return;
        }

        startMillis = System.currentTimeMillis();
        failedClicks = 0;
        failedText.setText(failedClicks + "");
        currentColor = colorList.get(rd.nextInt(colorList.size()));
        cv.setCardBackgroundColor(currentColor);

        handler = new Handler();
        handler.postDelayed(updater, 250);
    }

    @Override
    public void registerPacketHandlers() {
    }

    @Override
    public void onEnd() {
        if (handler != null)
            handler.removeCallbacks(updater);
    }

    private void onClick() {
        if (currentColor.equals(targetColor)) {
            handler.removeCallbacks(updater);
            long duration = System.currentTimeMillis() - startMillis;
            float punishment = Math.max(((10 - failedClicks) / 10f), 0);
            int score = (int)(100f / (duration / 200f + 8) * 8 * punishment * punishment);
            Log.d("SpeedQuest", "Score: " + score);
            finish(score);
        } else {
            failedClicks++;
            failedText.setText(failedClicks + "");
        }
    }

    private void onUpdateColor() {
        Integer nextColor = currentColor;

        while (currentColor.equals(nextColor))
            nextColor = colorList.get(rd.nextInt(colorList.size()));

        currentColor = nextColor;
        cv.setCardBackgroundColor(currentColor);
        long deltaMillis = System.currentTimeMillis() - startMillis;
        handler.postDelayed(updater, Math.min(250 + (int)(200 * (deltaMillis / 6000f)), 500));
    }
}
