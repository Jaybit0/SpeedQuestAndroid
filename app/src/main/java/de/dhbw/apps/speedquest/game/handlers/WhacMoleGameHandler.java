package de.dhbw.apps.speedquest.game.handlers;

import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import java.util.Random;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class WhacMoleGameHandler extends GameHandler {

    final int LENGTH = 9;

    private int currRound = 0;
    private int rating = 0;
    private Random rd;
    private Handler handler;
    private int currBtnIndex = -1;
    private ImageButton[] buttons = new ImageButton[LENGTH];
    private Runnable updater = this::timeEnds;
    private Runnable updateNextRound = this::nextRound;

    private int badIcon = -1;
    private int goodIcon = -1;

    public WhacMoleGameHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_whacmole;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        rating = 0;
        double seedDouble = task.getParam("seed", (double) new Random().nextInt());
        rd = new Random((int)seedDouble);
        handler = new Handler();

        for(int i = 0; i < LENGTH; i++){
            ImageButton btn = getButtonByNumber(i - 1, inflatedView);
            buttons[i] = btn;
            btn.setOnClickListener(this::onClick);
        }
        updateGameField(-1);
        handler.postDelayed(updateNextRound, rd.nextInt(750) + 250);
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {
        if(handler != null)
            handler.removeCallbacksAndMessages(null);
    }

    private void nextRound(){
        goodIcon = rd.nextInt(3);
        badIcon = rd.nextInt(2);
        if(badIcon == goodIcon)
            badIcon++;

        int index = rd.nextInt(LENGTH);
        updateGameField(index);
        handler.postDelayed(updater, 1200 - currRound * 100);
    }

    private void onClick(View v) {
        if(currBtnIndex == -1)
            return;

        if(buttons[currBtnIndex].equals(v)){
            handler.removeCallbacksAndMessages(null);
            rating += 10;
            sendScore(rating);
            currRound++;
            timeEnds();
        }
    }

    private void updateGameField(int newIndex){
        currBtnIndex = newIndex;
        for(int i = 0; i < LENGTH; i++){
            updateImage(i, newIndex != i);
        }
    }

    private void updateImage(int index, boolean good){
        int iconIndex = good ? goodIcon : badIcon;
        int color = rd.nextInt(3);
        iconIndex += color * 3;
        int id = activity.getResources().getIdentifier("ic_whac_" + iconIndex, "drawable", activity.getPackageName());
        buttons[index].setImageResource(id);
    }

    private void timeEnds(){
        int index = currBtnIndex;
        currBtnIndex = -1;
        updateImage(index, true);

        handler.postDelayed(updateNextRound, rd.nextInt(750) + 250);
    }

    private ImageButton getButtonByNumber(int number, View v){
        int btnId = activity.getResources().getIdentifier(
                "whaleButton" + number,
                "id",
                activity.getPackageName()
        );
        return v.findViewById(btnId);
    }
}
