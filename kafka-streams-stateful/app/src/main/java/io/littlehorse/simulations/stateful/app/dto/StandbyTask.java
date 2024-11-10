package io.littlehorse.simulations.stateful.app.dto;

public class StandbyTask {

    private String id;
    private long latestOffset;
    private long currentOffset;

    public StandbyTask(String id, long latestOffset, long currentOffset) {
        this.id = id;
        this.latestOffset = latestOffset;
        this.currentOffset = currentOffset;
    }

    public StandbyTask(){

    }

    public String getId() {
        return id;
    }

    public long getLatestOffset() {
        return latestOffset;
    }

    public long getCurrentOffset() {
        return currentOffset;
    }
}
