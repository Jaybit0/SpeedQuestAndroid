package de.dhbw.apps.speedquest.game.handlers;

import android.graphics.Color;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class OpenSafeHandler extends GameHandler {

    private Random rd;
    private Handler handler;
    private Runnable updater = this::resetLED;
    private Button number0;
    private Button number1;
    private Button number2;
    private Button number3;
    private Button number4;
    private Button number5;
    private Button number6;
    private Button number7;
    private Button number8;
    private Button number9;
    private TextView code_display;
    private CardView led;
    private List<Integer> colorList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
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
        number0 = (Button) inflatedView.findViewById(R.id.button_0);
        number0.setOnClickListener(v -> onClick('0'));

        number1 = (Button) inflatedView.findViewById(R.id.button_1);
        number1.setOnClickListener(v -> onClick('1'));

        number2 = (Button) inflatedView.findViewById(R.id.button_2);
        number2.setOnClickListener(v -> onClick('2'));

        number3 = (Button) inflatedView.findViewById(R.id.button_3);
        number3.setOnClickListener(v -> onClick('3'));

        number4 = (Button) inflatedView.findViewById(R.id.button_4);
        number4.setOnClickListener(v -> onClick('4'));

        number5 = (Button) inflatedView.findViewById(R.id.button_5);
        number5.setOnClickListener(v -> onClick('5'));

        number6 = (Button) inflatedView.findViewById(R.id.button_6);
        number6.setOnClickListener(v -> onClick('6'));

        number7 = (Button) inflatedView.findViewById(R.id.button_7);
        number7.setOnClickListener(v -> onClick('7'));

        number8 = (Button) inflatedView.findViewById(R.id.button_8);
        number8.setOnClickListener(v -> onClick('8'));

        number9 = (Button) inflatedView.findViewById(R.id.button_9);
        number9.setOnClickListener(v -> onClick('9'));

        code_display = (TextView) inflatedView.findViewById(R.id.codeView);
        code_display.setText("");
        input = "";
        finished=false;

        led = ((CardView)inflatedView.findViewById(R.id.led_view));

        try {
            double seedDouble = task.getParam("seed", new Double(new Random().nextInt()));
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
                if (length <= code.length()) {

                    if (code.charAt(length) == number) {
                        input = input + number;
                        code_display.setText(input);

                        if (input.equals(code)) {
                            finished = true;
                            led.setCardBackgroundColor(Color.GREEN);
                            Log.d("SpeedQuest", "finished");
                            long duration = System.currentTimeMillis() - startMillis;
                            int score = (int) (100f / (duration / 200f) * 80);
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
                } else {
                    Log.d("SpeedQuest", "InvalidInputLength: " + input);
                }
            } catch (Exception e) {
                Log.e("SpeedQuest", "", e);
                return;
            }
        }
    }

    private void resetLED(){
        led.setCardBackgroundColor(0xA9A9A9);
    }
}
