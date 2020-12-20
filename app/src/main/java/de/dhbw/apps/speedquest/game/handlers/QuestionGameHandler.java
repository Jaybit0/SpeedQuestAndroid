package de.dhbw.apps.speedquest.game.handlers;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.function.Supplier;

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
        String question = getQuestion(task);
        TextView questionText = inflatedView.findViewById(R.id.questiontext);
        questionText.setText(question);

        double correct = task.getParam("correct", 1.0);
        numberCorrectAnswer = (int)correct;

        for(int i = 1; i <= ANSWERCOUNT; i++){
            String answer = getAnswer(i, task);
            Button b = getButtonByNumber(i);
            b.setText(answer);
            b.setOnClickListener(i == numberCorrectAnswer ? this::rightClicked : this::wrongClicked);
        }
    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {

    }

    private String getAnswer(int answerNumber, TaskInfo task){
        return getLocalizedText(task, "quiz_answer_%d_" + answerNumber,
                () -> getAnswerFromTask(answerNumber, task));
    }

    private String getQuestion(TaskInfo task){
        return getLocalizedText(task, "quiz_question_%d",
                () -> getQuestionFromTask(task));
    }

    private String getAnswerFromTask(int answerNumber, TaskInfo task){
        String answerId = "answer" + answerNumber;
        return task.getParam(answerId, answerId);
    }

    private String getQuestionFromTask(TaskInfo task){
        return task.getParam("question", "question");
    }

    @SuppressLint("DefaultLocale")
    private String getLocalizedText(TaskInfo task, String format, Supplier<String> fallback){
        if(task.hasParam("localizenumber")) {
            int localizenumber = (int) (double) task.getParam("localizenumber", 1.0);
            int resId = getStringResourceId(String.format(format, localizenumber));
            if(resId > 0)
                return activity.getResources().getString(resId);
            else
                return fallback.get();
        }else
            return fallback.get();
    }

    private int getStringResourceId(String name){
        return activity.getResources().getIdentifier(
                name,
                "string",
                activity.getPackageName()
        );
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
            Button b = getButtonByNumber(i);
            b.setEnabled(false);

            if(i == numberCorrectAnswer)
                b.setBackgroundColor(Color.GREEN);
        }
        finish(result);
    }

    private Button getButtonByNumber(int number){
        int btnId = activity.getResources().getIdentifier(
                "answerbutton" + number,
                "id",
                activity.getPackageName()
        );
        return activity.findViewById(btnId);
    }
}
