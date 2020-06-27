package om.unidev.uflow;

import com.unidev.polydata.domain.v3.BasicPoly;
import com.unidev.uflow.FlowCore;
import com.unidev.uflow.FlowProcessor;
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
        flowCore = new FlowCore();
        flowCore.setMqService(testMqService);
    }

    @Test
    public void testFlowCreation() {
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
                assertEquals( "123", config.fetch("test1", "NA"));
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

    private void increment(String queue) {
        invocations.put(queue, invocations.getOrDefault(queue, 0) + 1);
    }


}
