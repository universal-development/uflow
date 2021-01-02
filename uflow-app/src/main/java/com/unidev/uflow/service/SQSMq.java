package com.unidev.uflow.service;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidev.platform.common.exception.UnidevRuntimeException;
import com.unidev.uflow.FlowProcessor;
import com.unidev.uflow.Mq;
import com.unidev.uflow.model.FlowItem;
import com.unidev.uflow.model.FlowProcessors;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class SQSMq implements Mq {

    private final ObjectMapper objectMapper;

    private final SQSConnectionFactory connectionFactory;

    private final JmsTemplate jmsTemplate;

    private final FlowProcessors flowProcessors;

    @Override
    public void registerProcessor(String queue, FlowProcessor flowProcessor) {
        int concurrency = 1;
        Optional<FlowProcessors.ListeningItem> listeningItem = flowProcessors.fetchConfigByQueue(queue);
        if (listeningItem.isPresent()) {
            concurrency = listeningItem.get().getCount();
        }

        SQSListener sqsListener = SQSListener.builder()
                .sqsMq(this)
                .flowProcessor(flowProcessor)
                .connectionFactory(connectionFactory)
                .concurrentConsumers(concurrency)
                .queueName(queue)
                .objectMapper(objectMapper)
                .build();
        sqsListener.startListener();
    }

    @Override
    public void deRegisterProcessor(String queue, FlowProcessor flowProcessor) {

       throw new UnidevRuntimeException("Deregistration not supported");
    }

    @Override
    public void sendMessage(String queue, FlowItem flowItem) {
        try {
            sendRawMessage(queue, flowItem);
        } catch (JsonProcessingException e) {
            log.error("Failed to send message to queue {}", queue, e);
        }
    }

    private void sendRawMessage(String queue, Object message)
            throws JsonProcessingException {
        String jsonMessage = objectMapper.writeValueAsString(message);
        try {
            jmsTemplate.convertAndSend(queue, gzipCompress(jsonMessage.getBytes()));
        } catch (Throwable e) {
            log.warn("Failed to send message", e);
        }
    }
    public byte[] gzipCompress(byte[] uncompressedData) {
        byte[] result = new byte[]{};
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressedData.length);
             GZIPOutputStream gzipOS = new GZIPOutputStream(bos)) {
            gzipOS.write(uncompressedData);
            gzipOS.close();
            result = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public byte[] gzipUncompress(byte[] compressedData) {
        byte[] result = new byte[]{};
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPInputStream gzipIS = new GZIPInputStream(bis)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIS.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            result = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
