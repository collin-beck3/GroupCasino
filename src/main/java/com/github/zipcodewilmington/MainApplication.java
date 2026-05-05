package com.github.zipcodewilmington;

public class MainApplication {
    public static void main(String[] args) {
        ConsoleUI consoleUI = new ConsoleUI();
        Casino casino = new Casino(consoleUI);

        casino.run();
    }
}