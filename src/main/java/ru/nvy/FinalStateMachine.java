package ru.nvy;


import ru.nvy.models.TransitionFunction;
import ru.nvy.models.graph.Edge;
import ru.nvy.models.graph.GraphElement;
import ru.nvy.models.graph.Graph;

import java.util.*;

public class FinalStateMachine {

    /**
     * Валидация входного файла
     * @param str
     */
    public static void validateInputFile(String str) {
        if (str.isEmpty()) {
            throw new NoSuchElementException("Пустая строка!");
        }
        int i = 1, j = i;
        if (str.charAt(0) != 'q' && str.charAt(0) != 'f') {
            throw new IllegalArgumentException("Некорректно задано первое состояние в строке");
        }
        while (str.charAt(j) >= '0' && str.charAt(j) <= '9') {

            j++;
        }
        if (j == i) {
            throw new IllegalArgumentException("Некорректно задано первое состояние в строке J=I");
        }
        i = j;
        if (str.charAt(i) != ',') {
            throw new IllegalArgumentException("Нет разделителя");
        }
        i++;
        i++;
        if (str.charAt(i) != '=') {
            throw new IllegalArgumentException("Нет итогового перехода в строке");
        }
        i++;
        if (str.charAt(i) != 'q' & str.charAt(i) != 'f') {
            throw new IllegalArgumentException("Итоговый переход в несуществующее в алфавите состояние в строке");
        }
        i++;
        j = i;
        while (j < str.length() && str.charAt(j) >= '0' && str.charAt(j) <= '9') {
            j++;
        }
        if (j == i) {
            throw new IllegalArgumentException("Итоговый переход задан некорректно в строке:");
        }
    }

    /**
     * Парсинг из вводного файла после валидации строки
     * @param str
     * @return
     */
    public static TransitionFunction parseInputFile(String str) {
        String from = str.substring(0, str.indexOf(","));
        String character = str.substring(str.indexOf(",") + 1, str.lastIndexOf("="));
        Character symbol = character.charAt(0);
        String to = str.substring(str.lastIndexOf("=") + 1);
        return new TransitionFunction(from, symbol, to);
    }

    /**
     *
     * @param graph
     * @param string
     * @return
     */
    public static boolean solveString(Graph graph, String string) {
        GraphElement currentEntity = graph.getFirstGraphElement();
        for (int i = 0; i < string.length(); i++) {
            for (Edge edge : currentEntity.getEdges()) {
                if (edge.getCharacter().equals(string.charAt(i))) {
                    currentEntity = edge.getTo();
                }
            }
        }
        return currentEntity.getName().startsWith("f");
    }

    public static GraphElement getRepeatedGraphElement(Graph graph) {
        for (GraphElement graphElement : graph.getStates().values()) {
            if (!Utils.isDeterministic(graphElement)) {
                return graphElement;
            }
        }
        return null;
    }

    /**
     * Создаем новое имя для нашего правого состояния
     * @param graph
     * @param graphElements
     * @return
     */
    private static String createNewNameForRightState(Graph graph, Set<GraphElement> graphElements) {
        if (Utils.isFinalState(graphElements)) {
            return "f" + graph.getNumOfFinalStates();
        } else {
            return "g" + graph.getNumOfCommonStates();
        }
    }


    /**
     * Просто создаем новый граф элемент
     * @param name
     * @param graphElements
     * @return
     */
    private static GraphElement makeNewRightGraphElement(String name, Set<GraphElement> graphElements) {
        GraphElement newGraphElement = new GraphElement(name);
        for (GraphElement graphElement : graphElements) {
            for (Edge edge : graphElement.getEdges()) {
                Edge newEdge = new Edge(edge.getCharacter(), newGraphElement);
                if (graphElements.contains(edge.getTo())) {
                    newGraphElement.addEdge(newEdge);
                } else {
                    newGraphElement.addEdge(edge);
                }
            }
        }
        return newGraphElement;
    }


    public static void refactorCurrentGraphElement(Graph graph, GraphElement currentGraphElement) {
        GraphElement graphElement = graph.getStates().get(currentGraphElement.getName());  // текущие элемент графа
        Character badEdge = getIssueCharacterFrom(graphElement); // ???
        Set<GraphElement> sadGraphElement = new HashSet<>(); // создали новый массив повторяющихся элементов графа
        // идем по ребрам нашего плохого граф элемента
        for (Edge edge : graphElement.getEdges()) {
            if (edge.getCharacter().equals(badEdge))
                sadGraphElement.add(edge.getTo());
        }
        // создаем новое имя для нашего правого State
        String newNameForNextState = createNewNameForRightState(graph, sadGraphElement);
        // создаем новый элемент графа
        GraphElement newGraphElement = makeNewRightGraphElement(newNameForNextState, sadGraphElement);
        // стоп
        Map<GraphElement, Set<GraphElement>> map = mapGraph(graph);
        for (GraphElement e : map.keySet()) {
            if (map.get(e).containsAll(sadGraphElement)) {
                Object[] edges = e.getEdges().toArray();
                for (Object edge : edges) {
                    Character characterFrom = ((Edge) edge).getCharacter();
                    if (isReplaceableEdge(e, characterFrom, sadGraphElement, newGraphElement)) {
                        replaceEdge(graph, e, ((Edge) edge), new Edge(characterFrom, newGraphElement));
                    }
                }
            }
        }
    }

    private static boolean isReplaceableEdge(GraphElement entity, Character character, Collection<GraphElement> issueEntities, GraphElement newEntity) {
        Collection<GraphElement> forCharacter = new ArrayList<>();
        for (Edge edge : entity.getEdges()) {
            if (edge.getCharacter().equals(character)) forCharacter.add(edge.getTo());
        }
        forCharacter.add(entity);
        if (forCharacter.contains(newEntity)) return true;
        return forCharacter.containsAll(issueEntities);
    }

    private static void replaceEdge(Graph gp, GraphElement e, Edge oldEdge, Edge newEdge) {
        gp.removeEdge(e, oldEdge);
        gp.addNewGraphElement(e, newEdge);
    }

    private static Map<GraphElement, Set<GraphElement>> mapGraph(Graph gp) {
        Map<GraphElement, Set<GraphElement>> map = new HashMap<>();
        for (GraphElement entity : gp.getStates().values()) {
            HashSet<GraphElement> set = new HashSet<>();
            for (Edge e : entity.getEdges()) {
                set.add(e.getTo());
            }
            map.put(entity, set);
        }
        return map;
    }

    /***
     *
     * @param graphElement
     * @return
     */
    private static Character getIssueCharacterFrom(GraphElement graphElement) {
        Map<Character, Integer> chToNum = new HashMap<>();
        Character charBuff;
        for (Edge edge : graphElement.getEdges()) {
            charBuff = edge.getCharacter();
            if (chToNum.containsKey(charBuff)) {
                return charBuff;
            } else {
                chToNum.put(charBuff, 1);
            }
        }
        return null;
    }
}