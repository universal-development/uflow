package com.unidev.uflow;

import com.unidev.uflow.model.FlowItem;
import com.unidev.uflow.model.FlowModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Flows core
 */
@Slf4j
public class FlowCore {

    private Map<String, FlowProcessor> processors = new HashMap<>(); // queue : processor

    @Getter
    @Setter
    private MqService mqService;

    public void addProcessor(String queue, FlowProcessor flowProcessor) {
        if (fetchProcessor(queue).isPresent()) {
            log.warn("Queue listener already defined {}", queue);
            return;
        }
        processors.put(queue, flowProcessor);
        mqService.registerProcessor(queue, flowProcessor);
    }

    public void removeProcessor(String queue) {
        Optional<FlowProcessor> flowProcessor = fetchProcessor(queue);
        if (flowProcessor.isEmpty()) {
            log.warn("No queue listener for {}", queue);
        }
        mqService.deRegisterProcessor(queue, flowProcessor.get());
        processors.remove(queue);
    }

    public Optional<FlowProcessor> fetchProcessor(String queue) {
        return Optional.ofNullable(processors.get(queue));
    }

    /**
     * Begin processing of flow
     */
    public void processFlow(FlowModel flow) {
        Optional<String> initialQueue = flow.nextQueue(null);
        if (initialQueue.isEmpty()) {
            log.warn("Empty initial queue for flow {}", flow);
            return;
        }
        FlowItem flowItem = FlowItem.flowItem(flow);
        mqService.sendMessage(initialQueue.get(), flowItem);
    }

}
