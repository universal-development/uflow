package com.unidev.uflow.controller;

import com.unidev.uflow.FlowCore;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flows")
@AllArgsConstructor
public class FlowController {

    private final FlowCore flowCore;

    @GetMapping
    public List<String> listProcessors() {
        List<String> list = new ArrayList<>();
        flowCore.getProcessors().forEach( (queue, processor) -> list.add(queue + " : " + processor.getClass().getName()));
        return list;
    }

}
