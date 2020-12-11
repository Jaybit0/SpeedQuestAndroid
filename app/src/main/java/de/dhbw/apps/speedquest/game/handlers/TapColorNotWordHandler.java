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
    private Handler handler;
    private TextView textview;
    private Runnable updater = this::onUpdate;
    private Integer targetColor;
    private Integer currentColor;
    private List<Integer> colorList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
    private long startMillis;
    private int failedClicks;
    private TextView failedText;
    private boolean success;

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
        success = false;

        textview = (TextView) inflatedView.findViewById(R.id.coloredTextClickable);
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

            TextView tv = inflatedView.findViewById(R.id.textTaskColor);
            tv.setText(colorName.toUpperCase());
            tv.setTextColor(targetColor);
            failedText = inflatedView.findViewById(R.id.textMissclicks);
        } catch (Exception e) {
            Log.e("SpeedQuest", "", e);
            return;
        }

        startMillis = System.currentTimeMillis();
        failedClicks = 0;
        failedText.setText(failedClicks + "");

        handler = new Handler();
        handler.postDelayed(updater, 500);
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
        if(!success){
            if (currentColor.equals(targetColor)) {
                handler.removeCallbacks(updater);
                long duration = System.currentTimeMillis() - startMillis;
                float punishment = Math.max(((10 - failedClicks) / 10f), 0);
                int score = (int)(100f / (duration / 200f + 8) * 80 * punishment * punishment);
                Log.d("SpeedQuest", "Score: " + score);
                success = true;
                finish(score);
            } else {
                failedClicks++;
                failedText.setText(failedClicks + "");
            }
        }
    }

    private void onUpdate(){
        Integer selector = rd.nextInt(3);
        switch(selector){
            case 0:
                onUpdateColor();
                break;

            case 1:
                onUpdateText();
                break;

            case 2:
                onUpdatePosition();
                break;

            default:
                break;
        }
    }

    private void onUpdateColor() {
        Integer nextColor = currentColor;

        while (currentColor == nextColor)
            nextColor = colorList.get(rd.nextInt(colorList.size()));

        currentColor = nextColor;
        textview.setTextColor(currentColor);
        long deltaMillis = System.currentTimeMillis() - startMillis;
        handler.postDelayed(updater, Math.min(250 + (int)(200 * (deltaMillis / 6000f)), 500));
    }

    private void onUpdateText() {
        String nextText = textList.get(rd.nextInt(textList.size()));

        textview.setText(nextText);
        long deltaMillis = System.currentTimeMillis() - startMillis;
        handler.postDelayed(updater, Math.min(250 + (int)(200 * (deltaMillis / 6000f)), 500));
    }

    private void onUpdatePosition() {
        float x = rd.nextFloat();
        float y = Math.max(0.1f, rd.nextFloat());

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textview.getLayoutParams();
        params.verticalBias = x;
        params.horizontalBias = y;
        textview.setLayoutParams(params);

        long deltaMillis = System.currentTimeMillis() - startMillis;
        handler.postDelayed(updater, Math.min(250 + (int)(200 * (deltaMillis / 6000f)), 500));
    }
}
