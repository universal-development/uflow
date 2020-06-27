package com.unidev.uflow;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Flows core
 */
public class FlowCore {

    @Getter
    @Setter
    private Map<String, FlowProcessor> processors = new HashMap<>(); // queue : processor

    @Getter
    @Setter
    private MqService mqService;

    public Optional<FlowProcessor> fetchProcessor(String queue) {
        return Optional.ofNullable(processors.get(queue));
    }

    /**
     * Register processors in queue service
     */
    public void registerProcessors() {
        processors.forEach((queue, processor) -> mqService.registerProcessor(queue, processor));
    }
}
