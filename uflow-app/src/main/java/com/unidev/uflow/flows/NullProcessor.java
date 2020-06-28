package com.unidev.uflow.flows;

import com.unidev.uflow.FlowProcessor;
import com.unidev.uflow.Mq;
import com.unidev.uflow.model.FlowItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class NullProcessor extends FlowProcessor {

    public NullProcessor(Mq mqService) {
        super(mqService);
    }

    @Override
    public Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem) {
        return Optional.empty();
    }
}
