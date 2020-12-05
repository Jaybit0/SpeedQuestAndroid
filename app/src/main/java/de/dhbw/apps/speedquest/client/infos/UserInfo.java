package de.dhbw.apps.speedquest.client.infos;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("name")
    public String name;

    @SerializedName("state")
    public int state;

    @SerializedName("color")
    public String color;

    @SerializedName("score")
    public int score;

    @SerializedName("isHost")
    public boolean isHost;
}
