package ru.nvy;

import ru.nvy.service.GeneralService;

import java.io.*;

public class Main {
    private static final String PATH1 = "C:\\Users\\NVY\\Downloads\\L2_TestInput\\var1.txt";
    private static final String PATH2 = "C:\\Users\\NVY\\Downloads\\L2_TestInput\\test2.txt";
    private static final String PATH3 = "C:\\Users\\NVY\\Downloads\\L2_TestInput\\var3_nd.txt";

    public static void main(String[] args) throws IOException {
        // Понять только FSM
//        GeneralService.runApp(PATH3);
//        GeneralService.runApp(PATH1);
        GeneralService.runApp(PATH2);
    }
}