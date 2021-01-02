package com.unidev.uflow.service;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.message.SQSBytesMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidev.uflow.FlowProcessor;
import com.unidev.uflow.model.FlowItem;
import javax.jms.Message;
import javax.jms.MessageListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

@Log4j2
@AllArgsConstructor
@Data
@Builder
public class SQSListener implements MessageListener {

    private final SQSMq sqsMq;
    private final FlowProcessor flowProcessor;
    private final SQSConnectionFactory connectionFactory;
    private final int concurrentConsumers;
    private final String queueName;
    private final ObjectMapper objectMapper;

    public void startListener() {
        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConcurrentConsumers(concurrentConsumers);
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.setDestinationName(queueName);
        listenerContainer.setupMessageListener(this);
        listenerContainer.setSessionTransacted(false);
        listenerContainer.start();
    }

    @Override
    public void onMessage(Message message) {
        SQSBytesMessage bytesMessage = (SQSBytesMessage) message;
        try {
            FlowItem etlMessage = objectMapper
                    .readValue(new String(sqsMq.gzipUncompress(bytesMessage.getBodyAsBytes())),
                            FlowItem.class);
            flowProcessor.onMessage(queueName, etlMessage);
        }catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
