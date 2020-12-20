package de.dhbw.apps.speedquest.game.handlers;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class FastTypingHandler extends GameHandler {

    private Random rd;
    private TextView correctWord;
    private CardView led1;
    private CardView led2;
    private CardView led3;
    private String word1 = "NA";
    private String word2 = "NA";
    private String word3 = "NA";
    private EditText textInput;
    private long startMillis;
    private Boolean finished;
    private int round;

    public FastTypingHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_fasttyping;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        led1 = inflatedView.findViewById(R.id.led2);
        led2 = inflatedView.findViewById(R.id.led1);
        led3 = inflatedView.findViewById(R.id.led3);

        try {
            double seedDouble = task.getParam("seed", new Double(new Random().nextInt()));
            rd = new Random((int)seedDouble);
        } catch (Exception e) {
            Log.e("SpeedQuest", "", e);
            return;
        }

        word1 = getWord();
        word2 = getWord();
        word3 = getWord();

        correctWord = inflatedView.findViewById(R.id.showWord);
        correctWord.setText(word1);
        round = 1;

        textInput = inflatedView.findViewById(R.id.typedText);
        textInput.setText("");
        textInput.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                checkText(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        finished = false;
        startMillis = System.currentTimeMillis();
    }

    @Override
    public void registerPacketHandlers() {
    }

    @Override
    public void onEnd() {
    }

    private String getWord(){
        Integer selection = rd.nextInt(6);
        //return "Hallo";

        switch (selection){
            case 0:
                return activity.getResources().getString(R.string.fasttype_word_0);
            case 1:
                return activity.getResources().getString(R.string.fasttype_word_1);
            case 2:
                return activity.getResources().getString(R.string.fasttype_word_2);
            case 3:
                return activity.getResources().getString(R.string.fasttype_word_3);
            case 4:
                return activity.getResources().getString(R.string.fasttype_word_4);
            case 5:
                return activity.getResources().getString(R.string.fasttype_word_5);
            default:
                return "Hund";
        }


    }

    private void checkText(String input){
        if(!finished) {
            switch (round) {
                case 1:
                    if (input.equals(word1)) {
                        led1.setCardBackgroundColor(Color.GREEN);
                        correctWord.setText(word2);
                        textInput.setText("");
                        round = 2;
                    }
                    break;
                case 2:
                    if (input.equals(word2)) {
                        led2.setCardBackgroundColor(Color.GREEN);
                        correctWord.setText(word3);
                        textInput.setText("");
                        round = 3;
                    }
                    break;
                case 3:
                    if (input.equals(word3)) {
                        led3.setCardBackgroundColor(Color.GREEN);
                        textInput.setFocusable(false);
                        long duration = System.currentTimeMillis() - startMillis;
                        int score = (int) (100f / (duration / 200f +8) * 8);
                        Log.d("SpeedQuest", "Score: " + score);
                        finished = true;
                        finish(score);
                    }
                    break;
                default:
                    Log.e("SpeedQuest", "Invalid Round Count: " + round);
                    break;
            }
        }
    }
}
