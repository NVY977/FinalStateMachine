package ru.nvy;


import ru.nvy.models.TransitionFunction;
import ru.nvy.models.graph.Edge;
import ru.nvy.models.graph.GraphElement;
import ru.nvy.models.graph.Graph;

import java.util.*;

public class FinalStateMachine {

    /**
     * Валидация входного файла
     *
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
     *
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
     * Решение строки
     *
     * @param graph
     * @param string
     * @return
     */
    public static boolean solveString(Graph graph, String string) {
        GraphElement currentEntity = graph.getFirstGraphElement();
        int count=string.length();
        for (int i = 0; i < string.length(); i++) {
            for (Edge edge : currentEntity.getEdges()) {
                if (edge.getCharacter().equals(string.charAt(i))) {
                    currentEntity = edge.getTo();
                    count--;
                }
            }
        }
        if(currentEntity.getName().startsWith("f") && count ==0 ){
            return currentEntity.getName().startsWith("f");
        }
        return false;
    }

    /**
     * Находим повторяющиеся элементы
     *
     * @param graph
     * @return
     */
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
     *
     * @param graph
     * @param graphElements
     * @return
     */
    private static String createNewNameForCenteredState(Graph graph, Set<GraphElement> graphElements) {
        if (Utils.isFinalState(graphElements)) {
            return "f" + graph.getNumOfFinalStates();
        } else {
            return "s" + graph.getNumOfCommonStates();
        }
    }

    /**
     * Просто создаем новый граф элемент
     *
     * @param name
     * @param graphElements
     * @return
     */
    private static GraphElement makeNewCenteredGraphElement(String name, Set<GraphElement> graphElements) {
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
        Character badEdge = getRepeatedCharacter(graphElement);
        Set<GraphElement> sadGraphElement = new HashSet<>(); // создали новый массив повторяющихся элементов графа
        for (Edge edge : graphElement.getEdges()) {
            if (edge.getCharacter().equals(badEdge)) {
                sadGraphElement.add(edge.getTo());
            }
        }

        String newNameForNextState = createNewNameForCenteredState(graph, sadGraphElement);
        GraphElement newGraphElement = makeNewCenteredGraphElement(newNameForNextState, sadGraphElement);
        Map<GraphElement, Set<GraphElement>> map = mapGraph(graph);

        // keySet - возвращает: Set (хранящийся в наборе не по порядку), в котором хранятся значения ключей.
        for (GraphElement element : map.keySet()) {
            Object[] edges = element.getEdges().toArray();
            for (Object edge : edges) {
                Edge curEdge = ((Edge) edge);
                Character characterFrom = curEdge.getCharacter();
                if (isReplaceableEdge(element, characterFrom, sadGraphElement, newGraphElement)) {
                    Edge newEdge =  new Edge(characterFrom, newGraphElement);
                    replaceEdge(graph, element, curEdge, newEdge);
                }
            }
        }
    }


    /**
     * ??? Является ли это ребро заменяемым
     *
     * @param graphElement
     * @param character
     * @param issueEntities
     * @param newEntity
     * @return
     */
    private static boolean isReplaceableEdge(GraphElement graphElement, Character character, Collection<GraphElement> issueEntities, GraphElement newEntity) {
        Collection<GraphElement> graphElements = new ArrayList<>();
        for (Edge edge : graphElement.getEdges()) {
            if (edge.getCharacter().equals(character)) {
                graphElements.add(edge.getTo());
            }
        }
        graphElements.add(graphElement);
        if (graphElements.contains(newEntity)) {
            return true;
        }
        return graphElements.containsAll(issueEntities);
    }

    /**
     * Замена старого ребра на новое
     *
     * @param graph
     * @param graphElement
     * @param oldEdge
     * @param newEdge
     */
    private static void replaceEdge(Graph graph, GraphElement graphElement, Edge oldEdge, Edge newEdge) {
        graph.removeEdge(graphElement, oldEdge);
        graph.addNewGraphElement(graphElement, newEdge);
    }

    /**
     * Составляем Map из графа
     *
     * @param graph
     * @return
     */
    private static Map<GraphElement, Set<GraphElement>> mapGraph(Graph graph) {
        Map<GraphElement, Set<GraphElement>> map = new HashMap<>();
        for (GraphElement graphElement : graph.getStates().values()) {
            HashSet<GraphElement> edges = new HashSet<>();
            for (Edge edge : graphElement.getEdges()) {
                edges.add(edge.getTo());
            }
            map.put(graphElement, edges);
        }
        return map;
    }

    /**
     * Получение плохих ребер
     *
     * @param graphElement
     * @return
     */
    private static Character getRepeatedCharacter(GraphElement graphElement) {
        Map<Character, Integer> characterMap = new HashMap<>();
        Character charBuff;
        for (Edge edge : graphElement.getEdges()) {
            charBuff = edge.getCharacter();
            if (characterMap.containsKey(charBuff)) {
                return charBuff;
            } else {
                characterMap.put(charBuff, 1);
            }
        }
        return null;
    }
}