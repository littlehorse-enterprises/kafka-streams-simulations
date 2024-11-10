package io.littlehorse.simulations.stateful.app.dto;

import java.util.Collection;

public class Cluster {
    private String state;

    private Collection<Node> nodes;

    public Cluster(String state, Collection<Node> nodes) {
        this.state = state;
        this.nodes = nodes;
    }

    public Cluster(){

    }

    public String getState() {
        return state;
    }

    public Collection<Node> getNodes() {
        return nodes;
    }
}
