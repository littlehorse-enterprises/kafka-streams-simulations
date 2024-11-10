package io.littlehorse.simulations.stateful.app.dto;

import java.util.Collection;
import java.util.List;

public class Node {

    private String instanceId;

    private Collection<ActiveTask> activeTasks;

    private Collection<StandbyTask> standbyTasks;

    private NodeStatus status;

    private String clusterState;

    public Node(String instanceId, Collection<ActiveTask> activeTasks, Collection<StandbyTask> standbyTasks, String clusterState, NodeStatus status) {
        this.instanceId = instanceId;
        this.activeTasks = activeTasks;
        this.standbyTasks = standbyTasks;
        this.status = status;
        this.clusterState = clusterState;
    }

    public Node(){

    }

    public static Node unreachableHost() {
        return new Node("Not available", List.of(), List.of(), null, NodeStatus.DOWN);
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Collection<ActiveTask> getActiveTasks() {
        return activeTasks;
    }

    public Collection<StandbyTask> getStandbyTasks() {
        return standbyTasks;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public String getClusterState() {
        return clusterState;
    }
}
