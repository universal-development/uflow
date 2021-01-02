package com.unidev.uflow.model;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import lombok.Builder;
import lombok.Data;

@Data
public class FlowTrigger {

    public FlowTrigger() { }

    private String cron;

    @Builder.Default
    private int triggeredFlows = 1;

    private List<String> flows;

    @Data
    public static final class ScheduledFlowTrigger {
        private ScheduledFuture<?> future;
        int hashCode;
    }

}
