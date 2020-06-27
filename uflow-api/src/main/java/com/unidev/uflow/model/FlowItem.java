package com.unidev.uflow.model;

import com.unidev.polydata.domain.v3.BasicPoly;
import com.unidev.polydata.domain.v3.BasicPolyList;
import lombok.*;

import java.util.List;
import java.util.Optional;

/**
 * Item read processing queue
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FlowItem {

    private FlowModel flowModel;

    @Builder.Default
    private BasicPolyList data = BasicPolyList.newList();

    public void addItem(BasicPoly poly) {
        data.add(poly);
    }

    public Optional<BasicPoly> fetchPoly(String id) {
        return data.polyById(id);
    }

    public List<BasicPoly> fetchData() {
        return data.getList();
    }

    public static FlowItem flowItem(FlowModel flowModel) {
        return FlowItem.builder().flowModel(flowModel).build();
    }

}
