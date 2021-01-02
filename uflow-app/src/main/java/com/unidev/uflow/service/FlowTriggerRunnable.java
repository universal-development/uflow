package com.unidev.uflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.unidev.platform.Randoms;
import com.unidev.uflow.FlowCore;
import com.unidev.uflow.model.FlowModel;
import com.unidev.uflow.model.FlowTrigger;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.DirectoryScanner;

@Slf4j
@AllArgsConstructor
public class FlowTriggerRunnable implements Runnable {

    public static final String TRIGGER_FLOWS_MIN_KEY = "trigger_flows_min";
    public static final String TRIGGER_FLOWS_MAX_KEY = "trigger_flows_max";

    private final static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private FlowCore flowCore;

    private FlowTrigger flowTrigger;

    private Randoms randoms;

    @Override
    public void run() {
        log.info("Triggering job for {}", flowTrigger);
        // scan directory
        List<String> files = new ArrayList<>();

        for (String config : flowTrigger.getFlows()) {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(new File("."));
            scanner.setIncludes(new String[]{config});
            scanner.scan();
            String[] includedFiles = scanner.getIncludedFiles();
            files.addAll(Arrays.asList(includedFiles));
        }
        log.info("Scanned files: {}", files);

        if (files.isEmpty()) {
            log.warn("No flows found for {}", flowTrigger);
            return;
        }

        int triggerCount = flowTrigger.getTriggeredFlows();
        if (triggerCount <= 0) {
            triggerCount = 1;
        }
        for(int i = 0;i<triggerCount;i++) {
            // select random flows
            String flowFile = randoms.randomValue(files);
            FlowModel flowModel;
            try {
                flowModel = YAML_MAPPER.readValue(new File(flowFile), FlowModel.class);
            }catch (Exception e) {
                log.warn("Failed read flow {}", flowFile, e);
                continue;
            }
            // put flows on queue
            int min = flowModel.getConfig().fetch(TRIGGER_FLOWS_MIN_KEY, 1);
            int max = flowModel.getConfig().fetch(TRIGGER_FLOWS_MAX_KEY, 1);
            long count = randoms.randomValue(min, max);
            for (int flow = 0; flow < count; flow++) {
                log.info("Triggering {}", flow);
                try {
                    flowCore.processFlow(flowModel);
                } catch (Exception e) {
                    log.warn("Failed to process flow {}", flowFile, e);
                }
            }
        }
    }
}
