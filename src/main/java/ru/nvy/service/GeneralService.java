package ru.nvy.service;

import ru.nvy.Graph;
import ru.nvy.models.GraphElement;

import java.io.IOException;
import java.util.List;

public class GeneralService {
    public static void runApp(String path) throws IOException {
        List<String> elements = FileInput.readFile(path);
        Graph gp = new Graph();

        for (String element : elements) {
            Utils.validateInputFile(element);
            gp.addNewEdgeFromCommand(Utils.parseInputFile(element));
        }
        System.out.println("Before:\n" + gp);

        GraphElement repeatedGraphElements;

        while ((repeatedGraphElements = Utils.getIssueEntity(gp)) != null) {
            Utils.refactorCurrentGraphElement(gp, repeatedGraphElements);
        }

        System.out.println("After:\n" + gp);
        System.out.println(Utils.solveString(gp,"aa+cd*=357"));
        System.out.println(Utils.solveString(gp,"add"));
        System.out.println(Utils.solveString(gp,"acf"));
        System.out.println(Utils.solveString(gp,"abf"));
        System.out.println(Utils.solveString(gp,"ab"));
        System.out.println("\n");
    }
}

