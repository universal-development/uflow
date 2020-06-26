package com.unidev.uflow;

import com.unidev.uflow.model.FlowModel;
import com.unidev.uflow.model.WorkItem;

public interface FlowProcessor extends FlowItem {

    FlowItem process(FlowModel model, FlowItem item);

}