package ru.nvy.models.graph;

import java.util.Collection;
import java.util.HashSet;


/**
 * По сути класс предоставляющий совокупность {начальных вершин} и Edge{ребро + конечная вершина} + работа с ними
 */
public class GraphElement {
    private final String name;
    private final Collection<Edge> edges;

    public GraphElement(String name) {
        this.name = name;
        edges = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Edge edge : edges) {
            stringBuilder.append("From: ").append(name).append(" Edge: ")
                    .append(edge.getCharacter()).append(" To: ").append(edge.getTo().getName()).append("\n");
        }
        return stringBuilder.toString();
    }
}
