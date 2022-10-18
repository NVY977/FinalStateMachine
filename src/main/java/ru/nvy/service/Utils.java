package ru.nvy.service;


import ru.nvy.Graph;
import ru.nvy.models.TransitionFunction;
import ru.nvy.models.Edge;
import ru.nvy.models.GraphElement;

import java.util.*;

public class Utils {

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

    public static TransitionFunction parseInputFile(String str) {
        String from = str.substring(0, str.indexOf(","));
        String character = str.substring(str.indexOf(",") + 1, str.lastIndexOf("="));
        Character symbol = character.charAt(0);
        String to = str.substring(str.lastIndexOf("=") + 1);
        return new TransitionFunction(from, symbol, to);
    }

    public static void refactorCurrentGraphElement(Graph gp, GraphElement entityWithSomeName) {
        GraphElement entity = gp.getStates().get(entityWithSomeName.getName());
        Character ch = getIssueCharacterFrom(entity);
        if (ch == null) return;
        Set<GraphElement> issueEntities = new HashSet<>();
        for (Edge e : entity.getEdges()) {
            if (e.getCharacter().equals(ch))
                issueEntities.add(e.getTo());
        }
        String name = getRightNameForEntity(gp, issueEntities);
        GraphElement newRightEntity = makeNewRightEntity(name, issueEntities);
        Map<GraphElement, Set<GraphElement>> map = mapGraph(gp);
        for (GraphElement e : map.keySet()) {
            if (map.get(e).containsAll(issueEntities)) {
                Object[] edges = e.getEdges().toArray();
                for (Object edge : edges) {
                    Character characterFrom = ((Edge) edge).getCharacter();
                    if (isReplaceableEdge(e, characterFrom, issueEntities, newRightEntity)) {
                        if (issueEntities.contains(e) && ((Edge) edge).getTo().equals(e)) {
                            replaceWithCycle(gp, e, (Edge) edge, new Edge(characterFrom, newRightEntity));
                        } else {
                            replaceEdge(gp, e, ((Edge) edge), new Edge(characterFrom, newRightEntity));
                        }
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
        gp.addNewEntity(e, newEdge);
    }

    private static void replaceWithCycle(Graph gp, GraphElement e, Edge oldEdge, Edge newEdge) {
        gp.removeEdge(e, oldEdge);
        gp.addNewEntity(e, newEdge);
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

    private static Character getIssueCharacterFrom(GraphElement entity) {
        Map<Character, Integer> chToNum = new HashMap<>();
        Character charBuff;
        for (Edge e : entity.getEdges()) {
            charBuff = e.getCharacter();
            if (chToNum.containsKey(charBuff)) {
                return charBuff;
            } else {
                chToNum.put(charBuff, 1);
            }
        }
        return null;
    }


    private static GraphElement makeNewRightEntity(String name, Set<GraphElement> entities) {
        GraphElement entity = new GraphElement(name);
        for (GraphElement e : entities) {
            for (Edge edge : e.getEdges()) {
                if (entities.contains(edge.getTo())) {
                    entity.addEdge(new Edge(edge.getCharacter(), entity));
                } else {
                    entity.addEdge(edge);
                }
            }
        }
        return entity;
    }

    private static boolean containsFinalEntity(Set<GraphElement> entities) {
        for (GraphElement e : entities) {
            if (e.getName().startsWith("f")) return true;
        }
        return false;
    }

    private static String getRightNameForEntity(Graph gp, Set<GraphElement> entities) {
        if (containsFinalEntity(entities)) return "f" + gp.getNumOfFinalStates();
        else return "g" + gp.getNumOfCommonStates();
    }

    public static boolean isDeterminedEntity(GraphElement entity) {
        Set<Character> characters = new HashSet<>();
        for (Edge edge : entity.getEdges()) {
            Character currentCharacter = edge.getCharacter();
            if (characters.contains(currentCharacter)){
                return false;
            }
            characters.add(currentCharacter);
        }
        return true;
    }

    public static GraphElement getIssueEntity(Graph gp) {
        for (GraphElement entity : gp.getStates().values()) {
            if (!isDeterminedEntity(entity)) {
                return entity;
            }
        }
        return null;
    }

    public static boolean solveString(Graph gp, String string) {
        GraphElement currentEntity = gp.getFirstEntity();
        for (int i = 0; i < string.length(); i++) {
            for (Edge edge : currentEntity.getEdges()) {
                if (edge.getCharacter().equals(string.charAt(i))) {
                    currentEntity = edge.getTo();
                }
            }
        }
        return currentEntity.getName().startsWith("f");
    }
}