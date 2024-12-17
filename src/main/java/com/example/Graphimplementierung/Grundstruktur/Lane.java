package com.example.Graphimplementierung.Grundstruktur;

import com.example.Graphimplementierung.Grundstruktur.Nodes.ActivityNode;

import java.util.HashSet;
import java.util.Set;

public class Lane {

    private String id;
    private String name;
    private Set<ActivityNode> flowNodeRefs;  // Sammlung von FlowNode-Referenzen (Knoten, die dieser Lane zugeordnet sind)

    // Konstruktor
    public Lane(String id, String name) {
        this.id = id;
        this.name = name;
        this.flowNodeRefs = new HashSet<>();  // Set von Knoten initialisieren
    }

    // Getter und Setter für ID und Name
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Methode, um einen Knoten zu dieser Lane hinzuzufügen
    public void addFlowNodeRef(ActivityNode node) {
        this.flowNodeRefs.add(node);
    }

    // Methode, um alle FlowNode-Referenzen dieser Lane zu erhalten
    public Set<ActivityNode> getFlowNodeRefs() {
        return flowNodeRefs;
    }

    // Override der toString-Methode für eine aussagekräftige Darstellung
    @Override
    public String toString() {
        return "Lane{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", flowNodeRefs=" + flowNodeRefs +
                '}';
    }
}
