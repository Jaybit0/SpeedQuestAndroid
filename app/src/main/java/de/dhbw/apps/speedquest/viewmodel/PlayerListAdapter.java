package de.dhbw.apps.speedquest.viewmodel;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import de.dhbw.apps.speedquest.R;
import de.dhbw.apps.speedquest.client.SpeedQuestClient;
import de.dhbw.apps.speedquest.client.infos.UserInfo;
import de.dhbw.apps.speedquest.client.packets.PacketPlayerUpdate;

import static java.lang.String.*;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerListViewHolder> {

    PlayerViewMode formatState;
    List<UserInfo> currPlayers;

    public PlayerListAdapter(PlayerViewMode formatState){
        this.formatState = formatState;
        currPlayers = new ArrayList<>();
    }

    public void updateList(Collection<UserInfo> users){
        currPlayers.clear();
        currPlayers.addAll(users);
        notifyDataSetChanged();
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

    class PlayerListViewHolder extends RecyclerView.ViewHolder{
        ImageView leftImg;
        ImageView rightImg;
        TextView nameText;
        TextView scoreText;

        public PlayerListViewHolder(@NonNull View itemView) {
            super(itemView);
            leftImg = itemView.findViewById(R.id.leftImg);
            rightImg = itemView.findViewById(R.id.rightImg);
            nameText = itemView.findViewById(R.id.nameText);
            scoreText = itemView.findViewById(R.id.scoreText);
        }
    }

    public enum PlayerViewMode {
        FinishScreen {
            @Override
            public void fillItem(PlayerListViewHolder viewHolder, UserInfo player) {
                viewHolder.nameText.setText(player.name);
                viewHolder.nameText.setTextColor(Color.parseColor(player.color));
                viewHolder.scoreText.setText(format("%d Punkte", player.score));
            }
        },
        LobbyScreen {
            @Override
            public void fillItem(PlayerListViewHolder viewHolder, UserInfo player) {
                viewHolder.nameText.setText(player.name);
                viewHolder.nameText.setTextColor(Color.parseColor(player.color));
                viewHolder.rightImg.setImageResource(player.isHost ? android.R.drawable.ic_menu_edit : 0);
            }
        },
        IngameScreen {
            @Override
            public void fillItem(PlayerListViewHolder viewHolder, UserInfo player) {
                viewHolder.nameText.setText(player.name);
                viewHolder.nameText.setTextColor(Color.parseColor(player.color));
                viewHolder.scoreText.setText(format("%d Punkte", player.score));
            }
        };

        public abstract void fillItem(PlayerListViewHolder viewHolder, UserInfo player);
    }

}