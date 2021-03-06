package com.unidev.uflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.unidev.uflow.FlowCore;
import com.unidev.uflow.FlowProcessor;
import com.unidev.uflow.model.FlowItem;
import com.unidev.uflow.model.FlowModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/flows")
@AllArgsConstructor
@Slf4j
public class FlowController {

    private final FlowCore flowCore;

    private final ApplicationContext applicationContext;

    @Autowired
    private List<FlowProcessor> flowProcessorList;

    @GetMapping("listAvailableProcessors")
    public List<String> listAvailableProcessors() {
        List<String> list = new ArrayList<>();
        flowProcessorList.forEach(item -> list.add(item.getClass().getName()));
        return list;
    }

    @GetMapping("listRegisteredProcessors")
    public List<String> listRegisteredProcessors() {
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

    @PostMapping(value = "processFlowItem")
    @ApiResponse
    public String processFlowItem(@RequestParam String flowName, @Parameter(description = "Raw flow file") @RequestBody String message) {
        StringBuilder result = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        FlowProcessor processor;
        try {
            processor = (FlowProcessor) applicationContext.getBean(flowName);
        }catch (Exception e) {
            log.error("Failed to get bean",e);
            result.append("null processor for ").append(flowName);
            return result.toString();
        }

        try (InputStream in = IOUtils.toInputStream(message, "UTF-8")) {
            FlowModel flowModel = mapper.readValue(in, FlowModel.class);
            String firstQueue = "";
            if (flowModel.getFlow() != null && flowModel.getFlow().size() != 0) {
                firstQueue = flowModel.getFlow().get(0);
            }
            FlowItem build = FlowItem.builder()
                    .age(1)
                    .id(UUID.randomUUID().toString())
                    .flowModel(flowModel)
                    .build();
            processor.onMessage(firstQueue, build);

            result.append("\n").append("Output:").append("\n").append(mapper.writeValueAsString(build));

        } catch (Throwable e) {
            log.warn("Failed to load config file", e);
            result.append(ExceptionUtils.getStackTrace(e));
        }

        return result.toString();
    }

}
