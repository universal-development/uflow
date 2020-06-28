package com.unidev.uflow;

import com.unidev.uflow.model.FlowItem;

import java.util.HashMap;
import java.util.Map;

public class TestMqService implements MqService {

    private Map<String, FlowProcessor> processors = new HashMap<>();

    @Override
    public void registerProcessor(String queue, FlowProcessor flowProcessor) {
        processors.put(queue, flowProcessor);
    }

    @Override
    public void deRegisterProcessor(String queue, FlowProcessor flowProcessor) {
        processors.remove(queue);
    }

    @Override
    public void sendMessage(String queue, FlowItem flowItem) {
        processors.get(queue).onMessage(queue, flowItem);
    }
}
