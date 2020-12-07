package de.dhbw.apps.speedquest.client.packets.internal;

import de.dhbw.apps.speedquest.client.PacketID;
import de.dhbw.apps.speedquest.client.infos.TaskInfo;
import de.dhbw.apps.speedquest.client.packets.Packet;

@PacketID("internal.taskassign")
public class PacketTaskAssigned extends Packet {

    private TaskInfo assignedTask;

    public PacketTaskAssigned(TaskInfo info) {
        assignedTask = info;
    }

    public TaskInfo getAssignedTask() {
        return assignedTask;
    }

}
