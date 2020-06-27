package com.unidev.uflow;

import com.unidev.uflow.model.FlowItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Processor of queue elements.
 */
@AllArgsConstructor
@Slf4j
public abstract class  FlowProcessor {

    private MqService mqService;

    public void onMessage(String queue, FlowItem flowItem) {
        try {
            Optional<FlowItem> resultItem = processItem(queue, flowItem);
            if (resultItem.isEmpty()) {
                log.info("No response for {}, empty response", flowItem.getId());
                return;
            }
            FlowItem result = resultItem.get();
            Optional<String> nextQueue = result.getFlowModel().nextQueue(queue);
            if (nextQueue.isEmpty()) {
                log.info("No next queue for {} {} {}", flowItem.getId(), queue, flowItem);
                return;
            }
            mqService.sendMessage(nextQueue.get(), result);
        }catch (Exception e) {
            log.error("Failed to process message {}, {}", flowItem.getId(), flowItem, e);
        }
    }

    public abstract Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem);




}
