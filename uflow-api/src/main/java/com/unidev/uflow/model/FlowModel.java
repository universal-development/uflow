package com.unidev.uflow.model;

import com.unidev.platform.Strings;
import com.unidev.polydata.domain.v3.BasicPoly;
import lombok.*;

import java.util.List;
import java.util.Optional;

/**
 * Class which represent flow model.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FlowModel {

    private BasicPoly config;
    private List<String> flow;

    /**
     * Fetch next queue for processing.
     */
    public Optional<String> nextQueue(String currentQueue) {
        if (Strings.isEmpty(currentQueue)) {
            return Optional.of(this.flow.get(0));
        }
        for (int i = 0; i < this.flow.size() - 1; i++) {
            if (currentQueue.equalsIgnoreCase(this.flow.get(i))) {
                try {
                    return Optional.of(this.flow.get(i + 1));
                } catch (Throwable t) {
                    t.printStackTrace();
                    return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }

}
