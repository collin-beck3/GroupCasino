package com.github.zipcodewilmington.casino.games.trivia;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.utils.AnsiColor;
import com.github.zipcodewilmington.utils.IOConsole;

public class TriviaGame implements GameInterface {
    private PlayerInterface player;
    private IOConsole console = new IOConsole(AnsiColor.YELLOW);

    private static final String QUESTION_FILE = "/trivia-questions.json";
    private static final List<TriviaQuestion> QUESTIONS = loadQuestions();

    private static List<TriviaQuestion> loadQuestions() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream resource = TriviaGame.class.getResourceAsStream(QUESTION_FILE)) {
            if (resource == null) {
                throw new IllegalStateException("Could not find " + QUESTION_FILE + " on classpath.");
            }
            return mapper.readValue(resource, new TypeReference<List<TriviaQuestion>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load trivia questions from JSON", e);
        }
    }

    @Override
    public void add(PlayerInterface newPlayer) {
        this.player = newPlayer;
    }

    @Override
    public void remove(PlayerInterface oldPlayer) {
        this.player = null;
    }

    @Override
    public void run() {
        if (player == null) {
            console.println("No player has joined the trivia game.");
            return;
        }

        console.println("Welcome to Trivia! Answer the questions by typing the number of your choice.");

        int score = 0;

        for (int i = 0; i < QUESTIONS.size(); i++) {
            TriviaQuestion question = QUESTIONS.get(i);
            console.println("\nQuestion " + (i + 1) + ": " + question.getText());
            for (int choiceIndex = 0; choiceIndex < question.getChoices().length; choiceIndex++) {
                console.println((choiceIndex + 1) + ") " + question.getChoices()[choiceIndex]);
            }

            int answer = getAnswerFromUser(question.getChoices().length);
            if (answer == question.getCorrectAnswer()) {
                console.println("Correct!");
                score++;
            } else {
                console.println("Sorry, the correct answer was " + question.getCorrectAnswer() + ".");
            }
        }

        console.println("\nGame over! You scored " + score + " out of " + QUESTIONS.size() + ".");
    }

    private int getAnswerFromUser(int numberOfChoices) {
        while (true) {
            int answer = console.getIntegerInput("Enter the number of your answer (1-" + numberOfChoices + "): ");
            if (answer >= 1 && answer <= numberOfChoices) {
                return answer;
            }
            console.println("Please choose a number between 1 and " + numberOfChoices + ".");
        }
    }
}

