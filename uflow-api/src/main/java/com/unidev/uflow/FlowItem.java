package com.unidev.uflow;

import com.unidev.uflow.model.FlowModel;

/**
 * Item from processing flow
 */
public interface FlowItem {

    String id();

    void onCreate(FlowModel flowModel);

    void onStart(FlowModel flowModel);

    void onStop(FlowModel flowModel);

    void onDestroy(FlowModel flowModel);

}
