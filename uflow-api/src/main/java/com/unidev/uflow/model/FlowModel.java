package com.unidev.uflow.model;

import com.unidev.polydata.domain.v3.BasicPoly;
import com.unidev.uflow.FlowProcessor;
import com.unidev.uflow.FlowReader;
import com.unidev.uflow.FlowWriter;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FlowModel {

    private BasicPoly config;
    private BasicPoly reader;
    private BasicPoly writer;
    private BasicPoly processors;

    private transient FlowReader flowReader;
    private transient FlowWriter flowWriter;

    private transient List<FlowProcessor> processorList;

}
