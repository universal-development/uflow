package com.unidev.uflow;

import com.unidev.uflow.model.FlowModel;
import com.unidev.uflow.model.WorkItem;

public interface FlowReader extends FlowItem {

    WorkItem read(FlowModel model);

}
