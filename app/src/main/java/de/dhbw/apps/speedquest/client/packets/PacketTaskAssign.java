package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;

@PacketID("taskassign")
public class PacketTaskAssign extends Packet {

    @SerializedName("round")
    private int round;

    @SerializedName("task")
    private TaskInfo task;

    public int getRound() {
        return round;
    }

    public TaskInfo getTask() {
        return task;
    }

}
