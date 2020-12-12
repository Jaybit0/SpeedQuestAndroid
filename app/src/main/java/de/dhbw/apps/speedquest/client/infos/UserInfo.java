package de.dhbw.apps.speedquest.client.infos;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

    public static final UserInfo defaultUserSelf;

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

    static {
        defaultUserSelf = new UserInfo();
        defaultUserSelf.isHost = false;
        defaultUserSelf.color = "#ffffff";
        defaultUserSelf.name = "<unknown>";
        defaultUserSelf.score = 0;
        defaultUserSelf.state = 0;
    }
}
