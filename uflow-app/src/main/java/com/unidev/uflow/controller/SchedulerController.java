package com.unidev.uflow.controller;

import com.unidev.polydata.domain.v3.BasicPoly;
import com.unidev.uflow.FlowCore;
import com.unidev.uflow.service.SchedulerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduler")
@Slf4j
public class SchedulerController {

    @Autowired
    private SchedulerService schedulerService;

    @Value("${scheduler.enabled:true}")
    private Boolean enabled;

    @Value("${scheduler.configs}")
    private String[] configDirs;

    @GetMapping("configuration")
    public BasicPoly configuration() {
        BasicPoly result = BasicPoly
                .newPoly()
                .with("enabled", enabled)
                .with("configDirs", configDirs);

        return result;
    }

    @GetMapping("listTriggers")
    public String listTriggers() {
        StringBuilder result = new StringBuilder();
        schedulerService.getScheduledFutures().forEach( (key, value) -> {
            result.append(key).append(" : ").append(value).append("\n");
        });
        return result.toString();
    }



}
