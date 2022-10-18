package ru.nvy.service;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

public class FileInput {
    public static List<String> readFile(String path) throws NoSuchFileException {
        try {
            return Files.readAllLines(Path.of(path));
        } catch (Exception e) {
            throw new NoSuchFileException("Path is bad");
        }
    }
}
