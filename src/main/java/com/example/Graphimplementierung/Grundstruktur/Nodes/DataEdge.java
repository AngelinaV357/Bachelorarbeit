package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class DataEdge extends Edge {

    // Konstruktor für DataEdge
    public DataEdge(String id, Node source, Node target) {
        // Aufruf des Konstruktors der übergeordneten Edge-Klasse
        super(id, source, target, null); // Kein 'condition' für DataEdge nötig, falls nicht relevant
    }

    // Optionale Methode für spezifische Darstellung von DataEdge (z.B. zu Debugging-Zwecken)
    @Override
    public String toString() {
        return "DataEdge{source='" + getSource().getName() + "', target='" + getTarget().getName() + "'}";
    }
}
