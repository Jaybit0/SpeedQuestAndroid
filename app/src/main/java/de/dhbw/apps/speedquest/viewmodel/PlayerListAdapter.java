package de.dhbw.apps.speedquest.viewmodel;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.infos.UserIngameInfo;

import static java.lang.String.*;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerListViewHolder> {

    PlayerViewMode formatState;
    List<UserInfo> currPlayers;

    public PlayerListAdapter(PlayerViewMode formatState){
        this.formatState = formatState;
        currPlayers = new ArrayList<>();
    }

    public void updateList(Collection<? extends UserInfo> users){
        currPlayers.clear();
        currPlayers.addAll(users);
        currPlayers.sort(this::compare);
        notifyDataSetChanged();
    }

    private int compare(UserInfo a, UserInfo b){
        return b.score - a.score;
    }

    @NonNull
    @Override
    public PlayerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerListViewHolder holder, int position) {
        formatState.fillItem(holder, currPlayers.get(position));
    }

    @Override
    public int getItemCount() {
        return currPlayers.size();
    }

    static class PlayerListViewHolder extends RecyclerView.ViewHolder{
        ImageView leftImg;
        ImageView rightImg;
        TextView nameText;
        TextView scoreText;
        TextView newscoreText;

        public PlayerListViewHolder(@NonNull View itemView) {
            super(itemView);
            leftImg = itemView.findViewById(R.id.leftImg);
            rightImg = itemView.findViewById(R.id.rightImg);
            nameText = itemView.findViewById(R.id.nameText);
            scoreText = itemView.findViewById(R.id.scoreText);
            newscoreText = itemView.findViewById(R.id.newscoreText);
        }
    }

    public enum PlayerViewMode {
        FinishScreen {
            @Override
            public void fillItem(PlayerListViewHolder viewHolder, UserInfo player) {
                viewHolder.nameText.setText(player.name);
                viewHolder.nameText.setTextColor(Color.parseColor(player.color));
                viewHolder.scoreText.setText(
                        format(viewHolder.scoreText.getContext().getString(R.string.points_text),
                                player.score
                        )
                );
            }
        },
        LobbyScreen {
            @Override
            public void fillItem(PlayerListViewHolder viewHolder, UserInfo player) {
                viewHolder.nameText.setText(player.name);
                viewHolder.nameText.setTextColor(Color.parseColor(player.color));
                viewHolder.rightImg.setImageResource(player.isHost ? android.R.drawable.ic_menu_edit : 0);
                viewHolder.leftImg.setImageResource(player.state > 0 ? R.drawable.ic_online : R.drawable.ic_offline);
            }
        },
        IngameScreen {
            @Override
            public void fillItem(PlayerListViewHolder viewHolder, UserInfo player) {
                viewHolder.nameText.setText(player.name);
                viewHolder.nameText.setTextColor(Color.parseColor(player.color));
                viewHolder.scoreText.setText(
                        format(viewHolder.scoreText.getContext().getString(R.string.points_text),
                                player.score
                        )
                );
                if(player instanceof UserIngameInfo) {
                    UserIngameInfo ingame = (UserIngameInfo) player;
                    viewHolder.newscoreText.setText(
                            format("+%s", ingame.roundscore)
                    );
                }else{
                    viewHolder.newscoreText.setText("");
                }
            }
        };

        public abstract void fillItem(PlayerListViewHolder viewHolder, UserInfo player);
    }

}