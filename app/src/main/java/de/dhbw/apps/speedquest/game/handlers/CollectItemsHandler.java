package de.dhbw.apps.speedquest.game.handlers;

import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class CollectItemsHandler extends GameHandler {

    private Random rd;
    private Handler bananaHandler;
    private Handler mineHandler;
    private ImageView banana;
    private ImageView mine;
    private Runnable bananaUpdater = this::onUpdateBanana;
    private Runnable mineUpdater = this::onUpdateMine;
    private List<Integer> colorList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
    private long startMillis;
    private TextView collectedText;
    private int collected;

    public CollectItemsHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_collectitems;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        banana = (ImageView) inflatedView.findViewById(R.id.banana);
        banana.setOnClickListener(v -> onClickBanana());

        mine = (ImageView) inflatedView.findViewById(R.id.mine);
        mine.setOnClickListener(v -> onClickMine());

        collectedText = (TextView) inflatedView.findViewById(R.id.amountCollected);

        try {
            double seedDouble = task.getParam("seed", new Double(new Random().nextInt()));
            rd = new Random((int)seedDouble);
        } catch (Exception e) {
            Log.e("SpeedQuest", "", e);
            return;
        }

        startMillis = System.currentTimeMillis();
        collected = 0;
        collectedText.setText(Math.max(collected,0) + "");

        bananaHandler = new Handler();
        bananaHandler.postDelayed(bananaUpdater, 400);

        mineHandler = new Handler();
        mineHandler.postDelayed(mineUpdater, 600);

        mine.setClickable(false);
        mine.setVisibility(View.INVISIBLE);

        banana.setClickable(false);
        banana.setVisibility(View.INVISIBLE);
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {
        if (bananaHandler != null)
            bananaHandler.removeCallbacks(bananaUpdater);

        if (mineHandler != null)
            mineHandler.removeCallbacks(mineUpdater);

        long duration = System.currentTimeMillis() - startMillis;
        int score = (int)(100f - collected / (duration / 200f) * 80);
        Log.d("SpeedQuest", "Score: " + score);
        finish(score);
    }

    private void onClickBanana(){
        collected++;
        if(collected < 10){
            collectedText.setText(Math.max(collected,0) + "");
            banana.setClickable(false);
            banana.setVisibility(View.INVISIBLE);
        }else{
            collectedText.setText(Math.max(collected,0) + "");
            collectedText.setTextColor(Color.GREEN);
            bananaHandler.removeCallbacks(bananaUpdater);
            mineHandler.removeCallbacks(mineUpdater);

            mine.setClickable(false);
            mine.setVisibility(View.INVISIBLE);
            banana.setClickable(false);
            banana.setVisibility(View.INVISIBLE);

            long duration = System.currentTimeMillis() - startMillis;
            int score = (int)(100f - collected / (duration / 200f) * 80);
            Log.d("SpeedQuest", "Score: " + score);
            finish(score);
        }
    }

    private void onClickMine(){
        collected = collected - 3;
        collectedText.setText(Math.max(collected,0) + "");
        mine.setClickable(false);
        mine.setVisibility(View.INVISIBLE);
    }

    private void onUpdateMine(){
        if(mine.isClickable()){
            Boolean rand = rd.nextBoolean();
            if(rand){
                mine.setClickable(false);
                mine.setVisibility(View.INVISIBLE);
            }else{
                float x = rd.nextFloat();
                float y = Math.max(0.2f, rd.nextFloat());

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mine.getLayoutParams();
                params.verticalBias = y;
                params.horizontalBias = x;
                mine.setLayoutParams(params);
            }
        }else{
            float x = rd.nextFloat();
            float y = Math.max(0.2f, rd.nextFloat());

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mine.getLayoutParams();
            params.verticalBias = y;
            params.horizontalBias = x;
            mine.setLayoutParams(params);

            mine.setClickable(true);
            mine.setVisibility(View.VISIBLE);
        }

        long deltaMillis = System.currentTimeMillis() - startMillis;
        mineHandler.postDelayed(mineUpdater, Math.min(250 + (int)(200 * (deltaMillis / 6000f)), 1000));
    }

    private void onUpdateBanana(){
        if(banana.isClickable()){
            Boolean rand = rd.nextBoolean();
            if(rand){
                banana.setClickable(false);
                banana.setVisibility(View.INVISIBLE);
            }else{
                float x = rd.nextFloat();
                float y = Math.max(0.2f, rd.nextFloat());

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) banana.getLayoutParams();
                params.verticalBias = y;
                params.horizontalBias = x;
                banana.setLayoutParams(params);
            }
        }else{
            float x = rd.nextFloat();
            float y = Math.max(0.2f, rd.nextFloat());

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) banana.getLayoutParams();
            params.verticalBias = y;
            params.horizontalBias = x;
            banana.setLayoutParams(params);

            banana.setClickable(true);
            banana.setVisibility(View.VISIBLE);
        }

        long deltaMillis = System.currentTimeMillis() - startMillis;
        bananaHandler.postDelayed(bananaUpdater, Math.min(250 + (int)(200 * (deltaMillis / 6000f)), 1000));
    }
}
