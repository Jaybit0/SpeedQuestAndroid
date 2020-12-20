package de.dhbw.apps.speedquest.game.handlers;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.Random;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class OpenSafeHandler extends GameHandler {

    private Handler handler;
    private Runnable updater = this::resetLED;
    private TextView code_display;
    private CardView led;
    private long startMillis;
    private String code;
    private String input;
    private Boolean finished;

    public OpenSafeHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_opensafe;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        Button number0 = inflatedView.findViewById(R.id.button_0);
        number0.setOnClickListener(v -> onClick('0'));

        Button number1 = inflatedView.findViewById(R.id.button_1);
        number1.setOnClickListener(v -> onClick('1'));

        Button number2 = inflatedView.findViewById(R.id.button_2);
        number2.setOnClickListener(v -> onClick('2'));

        Button number3 = inflatedView.findViewById(R.id.button_3);
        number3.setOnClickListener(v -> onClick('3'));

        Button number4 = inflatedView.findViewById(R.id.button_4);
        number4.setOnClickListener(v -> onClick('4'));

        Button number5 = inflatedView.findViewById(R.id.button_5);
        number5.setOnClickListener(v -> onClick('5'));

        Button number6 = inflatedView.findViewById(R.id.button_6);
        number6.setOnClickListener(v -> onClick('6'));

        Button number7 = inflatedView.findViewById(R.id.button_7);
        number7.setOnClickListener(v -> onClick('7'));

        Button number8 = inflatedView.findViewById(R.id.button_8);
        number8.setOnClickListener(v -> onClick('8'));

        Button number9 = inflatedView.findViewById(R.id.button_9);
        number9.setOnClickListener(v -> onClick('9'));

        code_display = inflatedView.findViewById(R.id.codeView);

        input = "";
        finished=false;

        led = inflatedView.findViewById(R.id.led_view);

        Random rd;
        try {
            double seedDouble = task.getParam("seed", (double) new Random().nextInt());
            rd = new Random((int)seedDouble);
        } catch (Exception e) {
            Log.e("SpeedQuest", "", e);
            return;
        }

        code = String.valueOf(rd.nextInt(999999));
        ((TextView)inflatedView.findViewById(R.id.secret_code)).setText(code);

        startMillis = System.currentTimeMillis();
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {
        if (handler != null)
            handler.removeCallbacks(updater);
    }


    private void onClick(char number){
        int length = input.length();

        if(!finished) {
            try {
                    if (code.charAt(length) == number) {
                        input = input + number;
                        code_display.setText(input);

                        if (input.equals(code)) {
                            finished = true;
                            led.setCardBackgroundColor(Color.GREEN);
                            Log.d("SpeedQuest", "finished");
                            long duration = System.currentTimeMillis() - startMillis;
                            int score = (int) (100f / (duration / 200f + 8) * 8);
                            Log.d("SpeedQuest", "Score: " + score);
                            if (handler != null)
                                handler.removeCallbacks(updater);
                            finish(score);
                        }
                    } else {
                        code_display.setText("");
                        led.setCardBackgroundColor(Color.RED);
                        input = "";

                        handler = new Handler();
                        handler.postDelayed(updater, 500);
                    }
            } catch (Exception e) {
                Log.d("SpeedQuest", "", e);
            }
        }
    }

    private void resetLED(){
        led.setCardBackgroundColor(0xA9A9A9);
    }
}
