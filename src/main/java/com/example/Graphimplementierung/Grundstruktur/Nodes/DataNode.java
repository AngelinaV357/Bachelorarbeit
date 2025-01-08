package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class DataNode extends Node {
    private final String dataType;

    public DataNode(String id, String name, Lane lane, String dataType) {
        super(id, dataType, name, lane);
        this.dataType = dataType; // "DataObject" oder "DataInput"
    }

    public String getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        // Prüft, ob die Lane vorhanden und nicht "Unknown Lane" ist
        String laneInfo = (getLane() != null && !"Unknown Lane".equals(getLane().getName()))
                ? ", lane='" + getLane().getName() + "'"
                : "";

        return String.format("DataNode{id='%s', name='%s'%s, dataType='%s'}",
                getId(), getName(), laneInfo, dataType);
    }
}
