package de.dhbw.apps.speedquest.game.handlers;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class QuestionGameHandler extends GameHandler {

    final int ANSWERCOUNT = 4;

    int numberCorrectAnswer = 0;

    public QuestionGameHandler(IngameActivity activity){
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_question;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        String question = task.getParam("question", "Miss quest");

        TextView questionText = inflatedView.findViewById(R.id.questiontext);
        questionText.setText(question);

        double correct = task.getParam("correct", 1.0);
        numberCorrectAnswer = (int)correct;

        for(int i = 1; i <= ANSWERCOUNT; i++){
            String s = String.valueOf(i);
            String answer = task.getParam("answer" + s, s);

            Button b = getButtonByNumber(i, activity);
            b.setText(answer);
            b.setOnClickListener(i == numberCorrectAnswer ? this::rightClicked : this::wrongClicked);
            Log.d("Bla", answer + "  " + b.toString());
        }
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {

    }

    private void wrongClicked(View v){
        v.setBackgroundColor(Color.RED);
        showResult(0);
    }

    private void rightClicked(View v){
        showResult(100);
    }

    private void showResult(int result){
        for(int i = 1; i <= ANSWERCOUNT; i++){
            Button b = getButtonByNumber(i, activity);
            b.setEnabled(false);

            if(i == numberCorrectAnswer)
                b.setBackgroundColor(Color.GREEN);
        }
        finish(result);
    }

    private Button getButtonByNumber(int number, AppCompatActivity activity){
        int btnId = activity.getResources().getIdentifier(
                "answerbutton" + String.valueOf(number),
                "id",
                activity.getPackageName()
        );
        return activity.findViewById(btnId);
    }
}
