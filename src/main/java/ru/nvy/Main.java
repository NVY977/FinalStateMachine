package ru.nvy;

import ru.nvy.service.GeneralService;

import java.io.*;

public class Main {
    private static final String PATH1 = "C:\\Users\\NVY\\Standard\\Documents\\GitHub\\University\\TAYK\\L_2_MA\\src\\main\\java\\ru\\nvy\\testInput\\var1.txt";
    private static final String PATH2 = "C:\\Users\\NVY\\Standard\\Documents\\GitHub\\University\\TAYK\\L_2_MA\\src\\main\\java\\ru\\nvy\\testInput\\test2.txt";
    private static final String PATH3 = "C:\\Users\\NVY\\Standard\\Documents\\GitHub\\University\\TAYK\\L_2_MA\\src\\main\\java\\ru\\nvy\\testInput\\var3_nd.txt";

    public static void main(String[] args) throws IOException {
        // Понять только FSM
//        GeneralService.runApp(PATH3);
//        GeneralService.runApp(PATH1);
        GeneralService.runApp(PATH2);
    }
}