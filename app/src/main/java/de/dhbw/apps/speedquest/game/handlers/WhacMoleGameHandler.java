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

    int currRound = 0;
    int rating = 0;
    Random rd;
    Handler handler;
    int currBtnIndex = -1;
    ImageButton[] buttons = new ImageButton[LENGTH];
    private Runnable updater = this::timeEnds;
    private Runnable updateNextRound = this::nextRound;

    int badIcon = -1;
    int goodIcon = -1;

    public WhacMoleGameHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_whacmole;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        double seedDouble = task.getParam("seed", new Double(new Random().nextInt()));
        rd = new Random((int)seedDouble);
        handler = new Handler();

        for(int i = 0; i < LENGTH; i++){
            ImageButton btn = inflatedView.findViewById(R.id.whaleButton1 + i);
            buttons[i] = btn;
            updateImage(i, true);
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

    }

    private void nextRound(){
        goodIcon = rd.nextInt(3);
        badIcon = rd.nextInt(2);
        if(badIcon == goodIcon)
            badIcon++;

        int index = rd.nextInt(LENGTH);
        updateGameField(index);
        handler.postDelayed(updater, 1000 - currRound * 100);
    }

    private void onClick(View v) {
        if(currBtnIndex == -1)
            return;

        if(buttons[currBtnIndex].equals(v)){
            handler.removeCallbacksAndMessages(null);
            rating += 10;
            finish(rating);
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
        int id = activity.getResources().getIdentifier("ic_whac_" + String.valueOf(iconIndex), "drawable", activity.getPackageName());
        buttons[index].setImageResource(id);
    }

    private void timeEnds(){
        int index = currBtnIndex;
        currBtnIndex = -1;
        updateImage(index, true);

        handler.postDelayed(updateNextRound, rd.nextInt(750) + 250);
    }
}
