package com.unidev.uflow.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "processor")
@Data
public class FlowProcessors {

    private List<ListeningItem> listeners;

    @Data
    public static final class ListeningItem {

        private String processor;

        private String queue;

        private int count;

    }

    public Optional<ListeningItem> fetchConfigByQueue(String queue) {
        for (FlowProcessors.ListeningItem listener : getListeners()) {
            if (queue.equalsIgnoreCase(listener.getQueue())) {
                return Optional.of(listener);
            }
        }
        return Optional.empty();
    }

}
