package com.github.zipcodewilmington.casino.games.trivia;

public enum TriviaCategory {
    HISTORY("history-questions.json"),
    POP_CULTURE("pop-culture-questions.json"),
    TECHNOLOGY("technology-questions.json"),
    SCIENCE("science-questions.json"),
    SPORTS("sports-questions.json");

    private final String filename;

    TriviaCategory(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}