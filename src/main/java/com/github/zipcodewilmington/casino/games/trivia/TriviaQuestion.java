package com.github.zipcodewilmington.casino.games.trivia;

public class TriviaQuestion {

    private String text;
    private String[] choices;
    private int correctAnswer;

    public TriviaQuestion() {
        // Jackson requires a no-arg constructor
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
