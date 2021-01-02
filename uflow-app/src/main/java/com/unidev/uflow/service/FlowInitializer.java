package com.unidev.uflow.service;

import com.unidev.uflow.FlowCore;
import com.unidev.uflow.FlowProcessor;
import com.unidev.uflow.model.FlowProcessors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class FlowInitializer {

    private final FlowProcessors flowProcessors;

    private final ApplicationContext applicationContext;

    private final FlowCore flowCore;

    @PostConstruct
    public void initialize() {
        log.info("Loading processors {}", flowProcessors);
        flowProcessors.getListeners().forEach( item -> {
            log.info("Adding processor {} {}", item.getQueue(), item.getProcessor());
            FlowProcessor processor = (FlowProcessor) applicationContext.getBean(item.getProcessor());
            flowCore.addProcessor(item.getQueue(), processor);
        });
    }


}
