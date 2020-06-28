package com.unidev.uflow.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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


}
