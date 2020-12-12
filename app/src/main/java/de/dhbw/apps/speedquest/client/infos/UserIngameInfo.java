package de.dhbw.apps.speedquest.client.infos;

import com.google.gson.annotations.SerializedName;

public class UserIngameInfo extends UserInfo {

    @SerializedName("roundscore")
    public int roundscore;
}
