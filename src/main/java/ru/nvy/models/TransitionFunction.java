package ru.nvy.models;

/**
 * По сути класс который просто служит для записи
 * @param from
 * @param character
 * @param to
 */
public record TransitionFunction(String from, Character character, String to) { }
