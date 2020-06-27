package com.unidev.uflow;

import com.unidev.uflow.model.FlowModel;

/**
 * Processor of queue elements.
 */
public interface FlowProcessor {

    String id();

    void create();

    void destroy();

}
