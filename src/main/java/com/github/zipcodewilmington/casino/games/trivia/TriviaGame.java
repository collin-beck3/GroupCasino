package com.github.zipcodewilmington.casino.games.trivia;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.utils.AnsiColor;
import com.github.zipcodewilmington.utils.IOConsole;

public class TriviaGame implements GameInterface {
    private PlayerInterface player;
    private List<PlayerInterface> players = new ArrayList<>();
    private IOConsole console = new IOConsole(AnsiColor.YELLOW);

    @Override
    public void add(PlayerInterface newPlayer) {
        this.players.add(newPlayer);
        this.player = newPlayer;
    }

    @Override
    public void remove(PlayerInterface oldPlayer) {
        this.players.remove(oldPlayer);
        if (this.players.isEmpty()) {
            this.player = null;
        }
    }

    @Override
    public void run() {
        if (player == null) {
            console.println("No player has joined the trivia game.");
            return;
        }

        TriviaCategory category = selectCategory();
        List<TriviaQuestion> allQuestions = loadQuestions(category.getFilename());
        List<TriviaQuestion> selectedQuestions = new ArrayList<>(allQuestions);
        Collections.shuffle(selectedQuestions);
        selectedQuestions = selectedQuestions.subList(0, Math.min(5, selectedQuestions.size()));

        console.println("Welcome to Trivia! Answer the questions by typing the number of your choice.");

        int score = 0;

        for (int i = 0; i < selectedQuestions.size(); i++) {
            TriviaQuestion question = selectedQuestions.get(i);
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

        console.println("\nGame over! You scored " + score + " out of " + selectedQuestions.size() + ".");
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

    private TriviaCategory selectCategory() {
        console.println("Select a category:");
        TriviaCategory[] categories = TriviaCategory.values();
        for (int i = 0; i < categories.length; i++) {
            console.println((i + 1) + ") " + categories[i].name().replace("_", " ").toLowerCase());
        }
        int choice = getAnswerFromUser(categories.length);
        return categories[choice - 1];
    }

    private List<TriviaQuestion> loadQuestions(String filename) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream resource = TriviaGame.class.getResourceAsStream("/" + filename)) {
            if (resource == null) {
                throw new IllegalStateException("Could not find " + filename + " on classpath.");
            }
            return mapper.readValue(resource, new TypeReference<List<TriviaQuestion>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load trivia questions from JSON", e);
        }
    }

    public List<PlayerInterface> getPlayers() {
        return players;
    }
}

