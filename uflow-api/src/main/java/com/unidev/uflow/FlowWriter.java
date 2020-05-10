package com.unidev.uflow;

import com.unidev.uflow.model.FlowModel;
import com.unidev.uflow.model.WorkItem;

public interface FlowWriter extends FlowItem {

    void write(FlowModel flowModel, WorkItem workItem);
}
