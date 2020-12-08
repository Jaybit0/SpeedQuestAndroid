package de.dhbw.apps.speedquest.client.packets.internal;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.client.packets.Packet;

@PacketID("internal.taskassign")
public class PacketTaskAssigned extends Packet {

    private TaskInfo assignedTask;
    private int round;

    public PacketTaskAssigned(TaskInfo info, int round) {
        assignedTask = info;
        this.round = round;
    }

    public TaskInfo getAssignedTask() {
        return assignedTask;
    }

    public int getRound() {
        return round;
    }

}
