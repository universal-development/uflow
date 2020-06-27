package com.unidev.uflow;

import com.unidev.uflow.model.FlowItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Processor of queue elements.
 */
@Slf4j
public abstract class  FlowProcessor {

    @Getter
    @Setter
    private int oldAgeMessages = 100;

    private MqService mqService;

    public FlowProcessor(MqService mqService) {
        this.mqService = mqService;
    }

    public void onMessage(String queue, FlowItem flowItem) {
        try {
            if (flowItem.getAge() > oldAgeMessages) {
                log.warn("Too old flow item {} {}", flowItem.getId(), flowItem);
                return;
            }
            Optional<FlowItem> resultItem = processItem(queue, flowItem);
            if (resultItem.isEmpty()) {
                log.info("No response for {}, empty response", flowItem.getId());
                return;
            }
            FlowItem result = resultItem.get();
            result.setAge(flowItem.getAge() + 1);
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
