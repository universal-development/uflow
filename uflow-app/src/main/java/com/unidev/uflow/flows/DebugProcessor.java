package com.unidev.uflow.flows;

import com.unidev.uflow.FlowProcessor;
import com.unidev.uflow.Mq;
import com.unidev.uflow.model.FlowItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class DebugProcessor extends FlowProcessor {

    @Getter
    @Setter
    private boolean dropMessage = true;

    public DebugProcessor(Mq mqService) {
        super(mqService);
    }

    @Override
    public Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem) {
        log.info("Received message {} from queue {}", fromQueue, flowItem);
        if (dropMessage) {
            return Optional.empty();
        }
        return Optional.of(flowItem);
    }
}
