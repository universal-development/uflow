package com.unidev.uflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.unidev.uflow.FlowCore;
import com.unidev.uflow.model.FlowModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flows")
@AllArgsConstructor
@Slf4j
public class FlowController {

    private final FlowCore flowCore;


    @GetMapping
    public List<String> listProcessors() {
        List<String> list = new ArrayList<>();
        flowCore.getProcessors().forEach((queue, processor) -> list.add(queue + " : " + processor.getClass().getName()));
        return list;
    }

    @PostMapping(value = "sendRawMessage")
    @ApiResponse
    public String sendRawMessage(@RequestParam int count, @Parameter(description = "Raw flow file") @RequestBody String message) {
        StringBuilder result = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        for (int i = 0; i < count; i++) {
            try (InputStream in = IOUtils.toInputStream(message, "UTF-8")) {
                FlowModel flowModel = mapper.readValue(in, FlowModel.class);
                flowCore.processFlow(flowModel);
                result.append("Putting message ").append(i + 1).append("\n");
            } catch (Throwable e) {
                log.warn("Failed to load config file", e);
                result.append(ExceptionUtils.getStackTrace(e));
            }
        }
        return result.toString();
    }

}
