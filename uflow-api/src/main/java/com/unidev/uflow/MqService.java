package com.unidev.uflow;

import com.unidev.uflow.model.FlowItem;

/**
 * Interface for message queue interfaces.
 */
public interface MqService {

    /**
     * Register flow processor for queue
     */
    void registerProcessor(String queue, FlowProcessor flowProcessor);

    void deRegisterProcessor(String queue, FlowProcessor flowProcessor);

    /**
     * Put message on queue for processing.
     */
    void sendMessage(String queue, FlowItem flowItem);


}
