package ru.nvy.models.graph;

import ru.nvy.models.TransitionFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * Работа с элементами графа, подсчет конечных состояний, работа с элементами графа и ребрами
 */
public class Graph {
    private final HashMap<String, GraphElement> states;
    private int numOfCommonStates;
    private int numOfFinalStates;

    public Graph() {
        states = new HashMap<>();
        numOfCommonStates = 0;
        numOfFinalStates = 0;
    }

    public GraphElement getFirstGraphElement() {
        return states.get("q0");
    }

    // region Work with Edge
    public void addNewEdgeFromFile(TransitionFunction transitionFunction) {
        GraphElement stateFrom = createGraphElementString(transitionFunction.from());
        GraphElement stateTo = createGraphElementString(transitionFunction.to());
        Edge edge =  new Edge(transitionFunction.character(), stateTo);
        stateFrom.addEdge(edge);
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
    // endregion

    // region Create GraphElement
    public void addNewGraphElement(GraphElement graphElement, Edge newEdge) {
        createGraphElement(newEdge.getTo());
        graphElement.addEdge(newEdge);
    }

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
