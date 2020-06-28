package com.unidev.uflow.service;

import com.unidev.uflow.FlowProcessor;
import com.unidev.uflow.Mq;
import com.unidev.uflow.model.FlowItem;
import org.springframework.stereotype.Component;

@Component
public class SQSMq implements Mq {
    @Override
    public void registerProcessor(String queue, FlowProcessor flowProcessor) {

    }

    @Override
    public void deRegisterProcessor(String queue, FlowProcessor flowProcessor) {

    }

    @Override
    public void sendMessage(String queue, FlowItem flowItem) {

    }
}
