package de.dhbw.apps.speedquest.game.handlers;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class FindWordGameHandler extends GameHandler {

    int index = 1;
    TextView text1View;
    TextView text2View;

    public FindWordGameHandler(IngameActivity activity){
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_findword;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        String question = String.format(
                inflatedView.getResources().getString(R.string.findword_task),
                task.getParam("word", "Nne")
        );
        TextView questionText = inflatedView.findViewById(R.id.question_findword_task);
        questionText.setText(question);

        double correctDbl = task.getParam("intext", 1.0);
        index = (int)correctDbl;

        String text1 = task.getParam("text1", "Nne");
        text1View = inflatedView.findViewById(R.id.question_findword_text1);
        text1View.setText(text1);
        text1View.setOnClickListener(this::text1Clicked);

        String text2 = task.getParam("text2", "Nne");
        text2View = inflatedView.findViewById(R.id.question_findword_text2);
        text2View.setText(text2);
        text2View.setOnClickListener(this::text2Clicked);

    }

    @Override
    public void registerPacketHandlers() {

    }

    @Override
    public void onEnd() {

    }

    private void text1Clicked(View v){
        endTask(index == 1 ? 100 : 0);
    }

    private void text2Clicked(View v){
        endTask(index == 2 ? 100 : 0);
    }

    private void endTask(int rating){
        if(index == 1)
            text1View.setBackgroundColor(Color.GREEN);
        else
            text2View.setBackgroundColor(Color.GREEN);

        text1View.setOnClickListener(null);
        text2View.setOnClickListener(null);
        finish(rating);
    }
}
