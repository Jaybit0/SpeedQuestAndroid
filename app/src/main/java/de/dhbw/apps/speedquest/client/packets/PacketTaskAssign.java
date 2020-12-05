package de.dhbw.apps.speedquest.client.packets;

import com.google.gson.annotations.SerializedName;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;

@PacketID("taskassign")
public class PacketTaskAssign extends Packet {

    @SerializedName("task")
    private TaskInfo task;

    public TaskInfo getTask() {
        return task;
    }

}
