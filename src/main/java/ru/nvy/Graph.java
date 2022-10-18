package ru.nvy;

import ru.nvy.models.Edge;
import ru.nvy.models.GraphElement;
import ru.nvy.models.TransitionFunction;

import java.util.HashMap;
import java.util.Map;

public class Graph {
    private final HashMap<String, GraphElement> states;
    private int numOfCommonStates;
    private int numOfFinalStates;

    public Graph() {
        states = new HashMap<>();
        numOfCommonStates = 0;
        numOfFinalStates = 0;
    }

    public GraphElement getFirstEntity() {
        return states.get("q0");
    }

    public void removeEdge(GraphElement from, Edge edge) {
        states.get(from.getName()).removeEdge(edge);
        String name = edge.getTo().getName();
        if (name.startsWith("f")) {
            numOfFinalStates--;
        }
        if (name.startsWith("q")) {
            numOfCommonStates--;
        }
    }


    public void addNewEdgeFromCommand(TransitionFunction transitionFunction) {
        GraphElement entityFrom = createGraphElementString(transitionFunction.from());
        GraphElement entityTo = createGraphElementString(transitionFunction.to());
        entityFrom.addEdge(new Edge(transitionFunction.character(), entityTo));
    }

    public void addNewEntity(GraphElement e, Edge newEdge) {
        createGraphElement(newEdge.getTo());
        e.addEdge(newEdge);
    }

    // region Create GraphElement
    public void createGraphElement(GraphElement entity) {
        String nameOfEntity = entity.getName();
        if (states.containsKey(nameOfEntity)) {
            return;
        }
        states.put(nameOfEntity, entity);
        checkGraphElement(nameOfEntity);
    }
    private GraphElement createGraphElementString(String name) {
        if (states.containsKey(name)) {
            return states.get(name);
        }
        GraphElement entity = new GraphElement(name);
        states.put(name, entity);
        checkGraphElement(name);
        return entity;
    }

    private void checkGraphElement(String str){
        if(str.startsWith("q")){
            numOfCommonStates++;
        } else if (str.startsWith("f")) {
            numOfFinalStates++;
        }
    }
    // endregion

    // region Get
    public Map<String, GraphElement> getStates() {
        return states;
    }

    public int getNumOfCommonStates() {
        return numOfCommonStates;
    }

    public int getNumOfFinalStates() {
        return numOfFinalStates;
    }
    // endregion

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (GraphElement graphElement : states.values()) {
            stringBuilder.append(graphElement.toString());
        }
        return stringBuilder.toString();
    }
}
