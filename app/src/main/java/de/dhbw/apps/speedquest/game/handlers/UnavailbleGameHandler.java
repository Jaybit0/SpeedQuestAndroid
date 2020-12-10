package de.dhbw.apps.speedquest.game.handlers;

import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.game.GameHandler;

public class UnavailbleGameHandler extends GameHandler {


    public UnavailbleGameHandler(IngameActivity activity) {
        super(activity);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_unknown;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo info) {
        if(info != null){
            TextView tv = inflatedView.findViewById(R.id.textUnknownGame);
            tv.setText(String.format("Unknown game: %s", info.getName()));
            tv.setOnClickListener(v -> {finish(10);});
        }
    }

    @Override
    public void registerPacketHandlers() {
    }

    @Override
    public void onEnd() {
    }
}
