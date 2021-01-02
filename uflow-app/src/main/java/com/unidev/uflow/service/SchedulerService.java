package com.unidev.uflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.unidev.platform.Randoms;
import com.unidev.uflow.FlowCore;
import com.unidev.uflow.model.FlowTrigger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SchedulerService {

    @Autowired
    private FlowCore flowCore;

    @Autowired
    private ThreadPoolTaskScheduler scheduledExecutorService;

    @Autowired
    private Randoms randoms;

    private ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Value("${scheduler.enabled:true}")
    @Getter
    @Setter
    private Boolean enabled;

    @Value("${scheduler.configs}")
    @Getter
    @Setter
    private String[] configDirs;

    @Getter
    private final Map<String, FlowTrigger.ScheduledFlowTrigger> scheduledFutures = new ConcurrentHashMap<>();


    @PostConstruct
    @Scheduled(fixedDelay = 30000)
    public void loadSchedulers() throws IOException {

        if (!enabled) {
            return;
        }
        log.info("Loading schedulers...");
        for (String dir : configDirs) {
            log.info("Loading {}", dir);

            File[] files = new File(dir).listFiles();

            if (files != null) {
                for (File triggerFile : files) {
                    createTrigger(triggerFile);
                }
            }
        }
    }

    public void cancelJob(String fileName) {
        scheduledFutures.get(fileName).getFuture().cancel(true);
    }

    private void createTrigger(File file) throws IOException {
        log.info("Creating trigger from: {}", file.getName());
        String fileContent = FileUtils.readFileToString(file, "UTF-8");
        int hashCode = fileContent.hashCode();

        if (scheduledFutures.containsKey(file.getName()) && scheduledFutures.get(file.getName()).getHashCode() != hashCode) {
            log.info("Content changed for {}", file.getName());
            cancelJob(file.getName());
        }

        if (scheduledFutures.containsKey(file.getName()) && scheduledFutures.get(file.getName()).getHashCode() == hashCode) {
            log.info("No content changed {}", file.getName());
            return;
        }

        try (FileInputStream in = new FileInputStream(file)) {
            FlowTrigger flowTrigger = yamlMapper.readValue(in, FlowTrigger.class);

            FlowTriggerRunnable flowTriggerRunnable = new FlowTriggerRunnable(
                    flowCore, flowTrigger, randoms
            );

            ScheduledFuture<?> scheduledFuture = scheduledExecutorService
                    .schedule(flowTriggerRunnable, new CronTrigger((flowTrigger.getCron())));

            FlowTrigger.ScheduledFlowTrigger scheduledJob = new FlowTrigger.ScheduledFlowTrigger();
            scheduledJob.setFuture(scheduledFuture);
            scheduledJob.setHashCode(hashCode);

            scheduledFutures.put(file.getName(), scheduledJob);

        } catch (Exception e) {
            log.warn("Failed to load job from {}", file.getName(), e);
            throw e;
        }
    }

}
