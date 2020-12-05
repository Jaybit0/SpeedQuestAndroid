package de.dhbw.apps.speedquest.client.infos;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TaskInfo {

    @SerializedName("name")
    private String name;

    @SerializedName("rating")
    private String rating;

    @SerializedName("parameters")
    private HashMap<String, Object> params;

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public Object getParam(String param) {
        return params.get(param);
    }

    public Set<Map.Entry<String, Object>> getParams() {
        return params.entrySet();
    }

}
