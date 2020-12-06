package de.dhbw.apps.speedquest.client.infos;

import com.google.gson.annotations.SerializedName;

public class StartInfo {

    @SerializedName("roundcount")
    private int rounds;

    public StartInfo() {
    }

    public StartInfo(int rounds) {
        this.rounds = rounds;
    }

    public int getRounds() {
        return rounds;
    }

}
