package de.dhbw.apps.speedquest.game.handlers;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class TapColorNotWordHandler extends GameHandler {

    private Random rd;
    private Handler colorHandler;
    private Handler textHandler;
    private Handler positionHandler;
    private TextView textview;
    private Runnable colorUpdater = this::onUpdateColor;
    private Runnable textUpdater = this::onUpdateText;
    private Runnable positionUpdater = this::onUpdatePosition;
    private Integer targetColor;
    private Integer currentColor;
    private List<Integer> colorList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
    private long startMillis;
    private int failedClicks;
    private TextView failedText;

    public TapColorNotWordHandler(IngameActivity activity) {
        super(activity);
        colorList.add(Color.RED);
        colorList.add(0xffffa500); // Orange
        colorList.add(Color.YELLOW);
        colorList.add(Color.GREEN);
        colorList.add(Color.CYAN);
        colorList.add(Color.BLUE);
        colorList.add(Color.MAGENTA);

        textList.add("RED");
        textList.add("ORANGE");
        textList.add("YELLOW");
        textList.add("GREEN");
        textList.add("CYAN");
        textList.add("BLUE");
        textList.add("MAGENTA");
    }

    @Override
    public int getGameResource() {
        return R.layout.game_tapcolornotword;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        textview = (TextView) inflatedView.findViewById(R.id.colorText);
        textview.setOnClickListener(v -> onClick());

        textview.setTextColor(Color.RED);
        textview.setText("RED");

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

            TextView tv = inflatedView.findViewById(R.id.textTaskColorTapColor);
            tv.setText(colorName.toUpperCase());
            tv.setTextColor(targetColor);
            failedText = inflatedView.findViewById(R.id.textTaskColorTapFailedCount);
        } catch (Exception e) {
            Log.e("SpeedQuest", "", e);
            return;
        }

        startMillis = System.currentTimeMillis();
        failedClicks = 0;
        failedText.setText(failedClicks + "");

        colorHandler = new Handler();
        colorHandler.postDelayed(colorUpdater, 200);

        textHandler = new Handler();
        textHandler.postDelayed(textUpdater, 300);

        positionHandler = new Handler();
        positionHandler.postDelayed(positionUpdater, 400);
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {
        if (colorHandler != null)
            colorHandler.removeCallbacks(colorUpdater);

        if (textHandler != null)
            textHandler.removeCallbacks(textUpdater);

        if (positionHandler != null)
            positionHandler.removeCallbacks(positionUpdater);
    }

    private void onClick() {
        if (currentColor.equals(targetColor)) {
            colorHandler.removeCallbacks(colorUpdater);
            textHandler.removeCallbacks(textUpdater);
            positionHandler.removeCallbacks(positionUpdater);
            long duration = System.currentTimeMillis() - startMillis;
            float punishment = Math.max(((10 - failedClicks) / 10f), 0);
            int score = (int)(100f / (duration / 200f + 8) * 80 * punishment * punishment);
            Log.d("SpeedQuest", "Score: " + score);
            finish(score);
        } else {
            failedClicks++;
            failedText.setText(failedClicks + "");
        }
    }

    private void onUpdateColor() {
        Integer nextColor = currentColor;

        while (currentColor == nextColor)
            nextColor = colorList.get(rd.nextInt(colorList.size()));

        currentColor = nextColor;
        textview.setTextColor(currentColor);
        long deltaMillis = System.currentTimeMillis() - startMillis;
        colorHandler.postDelayed(colorUpdater, Math.max(250 + (int)(200 * (deltaMillis / 6000f)), 700));
    }

    private void onUpdateText() {
        String nextText = textList.get(rd.nextInt(textList.size()));

        textview.setText(nextText);
        long deltaMillis = System.currentTimeMillis() - startMillis;
        textHandler.postDelayed(textUpdater, Math.max(250 + (int)(200 * (deltaMillis / 6000f)), 700));
    }


    private void onUpdatePosition() {
        float x = rd.nextFloat();
        float y = Math.max(0.1f, rd.nextFloat());

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textview.getLayoutParams();
        params.verticalBias = x;
        params.horizontalBias = y;
        textview.setLayoutParams(params);

        long deltaMillis = System.currentTimeMillis() - startMillis;
        positionHandler.postDelayed(positionUpdater, Math.max(250 + (int)(200 * (deltaMillis / 6000f)), 1000));
    }

}
