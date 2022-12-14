package ru.nvy;

import ru.nvy.models.graph.Edge;
import ru.nvy.models.graph.GraphElement;

import java.util.HashSet;
import java.util.Set;

public class Utils {

    /**
     * Проверка на финальное состояние
     * @param graphElements
     * @return
     */
    public static boolean isFinalState(Set<GraphElement> graphElements) {
        for (GraphElement graphElement : graphElements) {
            if (graphElement.getName().startsWith("f")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка на детерминированность
     * @param graphElement
     * @return
     */
    public static boolean isDeterministic(GraphElement graphElement) {
        Set<Character> characters = new HashSet<>();
        for (Edge edge : graphElement.getEdges()) {
            Character currentCharacter = edge.getCharacter();
            if (characters.contains(currentCharacter)) {
                return false;
            }
            characters.add(currentCharacter);
        }
        return true;
    }
}
