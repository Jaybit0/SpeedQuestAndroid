package de.dhbw.apps.speedquest.game.handlers;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
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
    private Handler banana1Handler;
    private Handler banana2Handler;
    private Handler banana3Handler;
    private Handler mineHandler;
    private ImageView banana1;
    private ImageView banana2;
    private ImageView banana3;
    private ImageView mine;
    private Runnable banana1Updater = this::restartBanana1;
    private Runnable banana2Updater = this::restartBanana2;
    private Runnable banana3Updater = this::restartBanana3;
    private Runnable mineUpdater = this::restartMine;
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
        banana1 = (ImageView) inflatedView.findViewById(R.id.banana1);
        banana1.setOnClickListener(v -> onClickBanana(banana1));
        banana1.setVisibility(View.VISIBLE);
        banana1.setClickable(true);

        banana2 = (ImageView) inflatedView.findViewById(R.id.banana2);
        banana2.setOnClickListener(v -> onClickBanana(banana2));
        banana2.setVisibility(View.VISIBLE);
        banana2.setClickable(true);

        banana3 = (ImageView) inflatedView.findViewById(R.id.banana3);
        banana3.setOnClickListener(v -> onClickBanana(banana3));
        banana3.setVisibility(View.VISIBLE);
        banana3.setClickable(true);

        mine = (ImageView) inflatedView.findViewById(R.id.mine);
        mine.setOnClickListener(v -> onClickMine());
        mine.setVisibility(View.VISIBLE);
        mine.setClickable(true);

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

        banana1 = (ImageView) inflatedView.findViewById(R.id.banana1);
        banana2 = (ImageView) inflatedView.findViewById(R.id.banana2);
        banana3 = (ImageView) inflatedView.findViewById(R.id.banana3);
        mine = (ImageView) inflatedView.findViewById(R.id.mine);

        banana1Handler = new Handler();
        banana1Handler.postDelayed(banana1Updater, 0 + rd.nextInt(1000));

        banana2Handler = new Handler();
        banana2Handler.postDelayed(banana2Updater, 500 + rd.nextInt(1500));

        banana3Handler = new Handler();
        banana3Handler.postDelayed(banana3Updater, 1000 + rd.nextInt(2000));

        mineHandler = new Handler();
        mineHandler.postDelayed(mineUpdater, 1500 + rd.nextInt(1000));
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {
        if (banana1Handler != null)
            banana1Handler.removeCallbacks(banana1Updater);

        if (banana2Handler != null)
            banana2Handler.removeCallbacks(banana2Updater);

        if (banana3Handler != null)
            banana3Handler.removeCallbacks(banana3Updater);

        if (mineHandler != null)
            mineHandler.removeCallbacks(mineUpdater);
    }

    public void move(final ImageView view, int speed){
        ValueAnimator va = ValueAnimator.ofFloat(0.05f, 0.95f);
        int mDuration = speed;
        va.setDuration(mDuration);
        va.addUpdateListener(animation -> {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            params.verticalBias = (float)animation.getAnimatedValue();
            view.setLayoutParams(params);
        });
        va.start();
    }

    private void restartBanana1(){
        Integer speed = 1000 + rd.nextInt(1500);
        banana1.setVisibility(View.VISIBLE);
        banana1.setClickable(true);
        move(banana1, speed);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) banana1.getLayoutParams();
        params.horizontalBias = rd.nextFloat();
        banana1.setLayoutParams(params);
        banana1Handler.postDelayed(banana1Updater, speed + 100 + rd.nextInt(2000));
    }

    private void restartBanana2(){
        Integer speed = 1000 + rd.nextInt(1500);
        banana2.setVisibility(View.VISIBLE);
        banana2.setClickable(true);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) banana2.getLayoutParams();
        params.horizontalBias = rd.nextFloat();
        banana2.setLayoutParams(params);
        move(banana2, speed);
        banana2Handler.postDelayed(banana2Updater, speed + 100 + rd.nextInt(2000));
    }

    private void restartBanana3(){
        Integer speed = 1000 + rd.nextInt(1500);
        banana3.setVisibility(View.VISIBLE);
        banana3.setClickable(true);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) banana3.getLayoutParams();
        params.horizontalBias = rd.nextFloat();
        banana3.setLayoutParams(params);
        move(banana3, speed);
        banana3Handler.postDelayed(banana3Updater, speed + 100 + rd.nextInt(2000));
    }

    private void restartMine(){
        Integer speed = 1000 + rd.nextInt(1500);
        mine.setVisibility(View.VISIBLE);
        mine.setClickable(true);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mine.getLayoutParams();
        params.horizontalBias = rd.nextFloat();
        mine.setLayoutParams(params);
        move(mine, speed);
        mineHandler.postDelayed(mineUpdater, speed + 100 + rd.nextInt(2000));
    }

    private void onClickBanana(ImageView view){
        collected++;
        collectedText.setText(Math.max(collected,0) + "");
        view.setClickable(false);
        view.setVisibility(View.INVISIBLE);
        Log.d("SpeedQuest", "Collected: " + collected);
        finish(collected);
    }

    private void onClickMine(){
        collected = collected - 3;
        collectedText.setText(Math.max(collected,0) + "");
        mine.setClickable(false);
        mine.setVisibility(View.INVISIBLE);
        finish(collected);
    }
}
