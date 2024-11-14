package io.littlehorse.simulations.stateful.api;

import lombok.Data;

import java.util.Map;

@Data
public class State {
    private final String state;
    private final Map<String, Integer> data;
}
