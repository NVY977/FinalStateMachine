package ru.nvy.service;

import ru.nvy.models.graph.Graph;
import ru.nvy.FinalStateMachine;
import ru.nvy.models.graph.GraphElement;
import ru.nvy.models.TransitionFunction;

import java.io.IOException;
import java.util.List;

public class GeneralService {
    public static void runApp(String path) throws IOException {
        List<String> elements = FileInput.readFile(path);
        Graph graph = new Graph();

        for (String element : elements) {
            FinalStateMachine.validateInputFile(element);
            TransitionFunction transitionFunction = FinalStateMachine.parseInputFile(element);
            graph.addNewEdgeFromFile(transitionFunction);
        }
        System.out.println("Before:\n" + graph);

        GraphElement repeatedGraphElements;

        while ((repeatedGraphElements = FinalStateMachine.getRepeatedGraphElement(graph)) != null) {
            FinalStateMachine.refactorCurrentGraphElement(graph, repeatedGraphElements);
        }

        System.out.println("After:\n" + graph);
        System.out.println(FinalStateMachine.solveString(graph,"aa+cd*=357"));
        System.out.println(FinalStateMachine.solveString(graph,"add"));
        System.out.println(FinalStateMachine.solveString(graph,"acf"));
        System.out.println(FinalStateMachine.solveString(graph,"abf"));
        System.out.println(FinalStateMachine.solveString(graph,"ab"));
        System.out.println("\n");
    }
}

