package com.unidev.uflow;

import com.unidev.polydata.domain.v3.BasicPoly;
import com.unidev.uflow.model.FlowItem;
import com.unidev.uflow.model.FlowModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;


public class FlowCoreTest {

    private FlowCore flowCore;
    private TestMqService testMqService;

    private Map<String, Integer> invocations;

    @BeforeEach
    public void init() {
        invocations = new ConcurrentHashMap<>();
        testMqService = new TestMqService();
        flowCore = new FlowCore(testMqService);
    }

    @Test
    public void testFlowProcessing() {
        flowCore.addProcessor("test1", new FlowProcessor(testMqService) {
            @Override
            public Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem) {
                increment(fromQueue);
                flowItem.getFlowModel().getConfig().put("test1", "123");
                return Optional.of(flowItem);
            }
        });

        flowCore.addProcessor("test2", new FlowProcessor(testMqService) {
            @Override
            public Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem) {
                BasicPoly config = flowItem.getFlowModel().getConfig();
                assertNotNull(config);
                assertEquals("123", config.fetch("test1", "NA"));
                increment(fromQueue);
                return Optional.of(flowItem);
            }
        });

        FlowModel flowModel = FlowModel.builder()
                .config(BasicPoly.newPoly())
                .flow(Arrays.asList("test1", "test2"))
                .build();

        flowCore.processFlow(flowModel);
        assertEquals(1, (int) invocations.get("test1"));
        assertEquals(1, (int) invocations.get("test2"));

    }

    @Test
    public void testMessageDropOnFirstProcessor() {
        flowCore.addProcessor("test1", new FlowProcessor(testMqService) {
            @Override
            public Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem) {
                increment(fromQueue);

                return Optional.empty();
            }
        });

        flowCore.addProcessor("test2", new FlowProcessor(testMqService) {
            @Override
            public Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem) {
                assertTrue(false, "Unreachable processor");
                return Optional.of(flowItem);
            }
        });

        FlowModel flowModel = FlowModel.builder()
                .config(BasicPoly.newPoly())
                .flow(Arrays.asList("test1", "test2"))
                .build();

        flowCore.processFlow(flowModel);
        assertEquals(1, (int) invocations.get("test1"));
        assertFalse(invocations.containsKey("test2"));

    }

    @Test
    public void testMessageDropping() {
        flowCore.addProcessor("test1", new FlowProcessor(testMqService) {
            @Override
            public Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem) {
                increment(fromQueue);
                flowItem.setAge(100);
                return Optional.of(flowItem);
            }
        });

        flowCore.addProcessor("test2", new FlowProcessor(testMqService) {
            @Override
            public Optional<FlowItem> processItem(String fromQueue, FlowItem flowItem) {
                increment(fromQueue);
                return Optional.of(flowItem);
            }
        });

        FlowModel flowModel = FlowModel.builder()
                .config(BasicPoly.newPoly())
                .flow(Arrays.asList("test1", "test2"))
                .build();
        flowCore.processFlow(flowModel);
        assertEquals(1, (int) invocations.get("test1"));
        assertFalse(invocations.containsKey("test2"));

    }

    private void increment(String queue) {
        invocations.put(queue, invocations.getOrDefault(queue, 0) + 1);
    }


}
