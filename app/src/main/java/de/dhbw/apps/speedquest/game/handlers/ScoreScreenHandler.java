package de.dhbw.apps.speedquest.game.handlers;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.List;

import de.dhbw.apps.speedquest.IngameActivity;
import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.SpeedQuestApplication;
import de.dhbw.apps.speedquest.client.GameCache;
import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.game.GameHandler;
import de.dhbw.apps.speedquest.viewmodel.PlayerListAdapter;

public class ScoreScreenHandler extends GameHandler {
    PlayerListAdapter adapter;
    public ScoreScreenHandler(IngameActivity activity) {
        super(activity);
        adapter = new PlayerListAdapter(PlayerListAdapter.PlayerViewMode.IngameScreen);
    }

    @Override
    public int getGameResource() {
        return R.layout.game_scores;
    }

    @Override
    public void initialize(View inflatedView, TaskInfo task) {
        SpeedQuestApplication app = (SpeedQuestApplication) activity.getApplication();
        GameCache cache = app.client.getGameCache();

        Collection<UserInfo> users = cache.getLastUserScores().isEmpty() ?
                cache.getUsers() :
                cache.getLastUserScores();
        RecyclerView listView = inflatedView.findViewById(R.id.playerList);

        listView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));

        adapter.updateList(users);
        listView.setAdapter(adapter);
    }

    @Override
    public void registerPacketHandlers() {
    }

    @Override
    public void onEnd() {
    }
}
