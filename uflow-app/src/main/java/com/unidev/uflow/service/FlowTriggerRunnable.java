package com.unidev.uflow.service;

import com.unidev.uflow.FlowCore;
import com.unidev.uflow.model.FlowTrigger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class FlowTriggerRunnable implements Runnable {

    private FlowCore flowCore;

    private FlowTrigger flowTrigger;

    @Override
    public void run() {
        log.info("Triggering job for {}", flowTrigger);
        // scan directory
        // select random flows
        // put flows on queue
    }
}
