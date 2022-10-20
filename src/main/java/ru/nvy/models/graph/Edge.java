package ru.nvy.models.graph;

/**
 * Создание ребра символ + конечный элемент
 */
public class Edge {
    private final Character character;
    private final GraphElement to;

    public Edge(Character c, GraphElement to) {
        this.character = c;
        this.to = to;
    }

    public Character getCharacter() {
        return character;
    }

    public GraphElement getTo() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        // если передаваемый объект является экземпляром Edge
        if (obj instanceof Edge edge) {
            return edge.getCharacter().equals(character) && edge.getTo().getName().equals(to.getName());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return character.hashCode()+to.getName().hashCode();
    }
}