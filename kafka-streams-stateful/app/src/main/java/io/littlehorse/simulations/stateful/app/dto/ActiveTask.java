package io.littlehorse.simulations.stateful.app.dto;

public class ActiveTask {

    private String id;
    private String threadName;
    private long latestOffset;
    private long currentOffset;

    public ActiveTask(String id, String threadName, long latestOffset, long currentOffset) {
        this.id = id;
        this.threadName = threadName;
        this.latestOffset = latestOffset;
        this.currentOffset = currentOffset;
    }

    public ActiveTask(){

    }

    public String getId() {
        return id;
    }

    public String getThreadName() {
        return threadName;
    }

    public long getLatestOffset() {
        return latestOffset;
    }

    public long getCurrentOffset() {
        return currentOffset;
    }
}
