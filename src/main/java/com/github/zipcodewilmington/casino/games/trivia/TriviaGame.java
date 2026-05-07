package com.github.zipcodewilmington.casino.games.trivia;

import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.utils.AnsiColor;
import com.github.zipcodewilmington.utils.IOConsole;

public class TriviaGame implements GameInterface {
    private PlayerInterface player;
    private IOConsole console = new IOConsole(AnsiColor.PURPLE);

    private static final String[] QUESTIONS = {
        "Where were the 2008 Summer Olympics held?",
        "What is the largest ocean on Earth?",
        "What is the capital of Iceland?",
        "What is Delaware's state dog?",
        "When was air conditioning invented?",
        "What was the first video game?",
        "Who's on first?",
        "Who painted the Mona Lisa?",
        "What is the capital of Australia?",
        "Which planet is closest to the Sun?",
        "What is the hardest natural substance on Earth?",
        "Which ocean is the smallest?",
        "What year did World War II end?",
        "Which element has the atomic number 1?",
        "What is the longest river in the world?",
        "Which movie features the character Darth Vader?",
        "What is the currency of Japan?",
        "Which gas do plants absorb from the atmosphere?",
        "What is the largest desert in the world?",
        "Who wrote 'Romeo and Juliet'?"
    };

    private static final String[][] ANSWER_CHOICES = {
        {"1) Athens", "2) Beijing", "3) London", "4) Sydney"},
        {"1) Atlantic Ocean", "2) Indian Ocean", "3) Pacific Ocean", "4) Arctic Ocean"},
        {"1) Reykjavik", "2) Copenhagen", "3) Oslo", "4) Stockholm"},
        {"1) Husky", "2) Pug", "3) Rescue Dog", "4) Beagle"},
        {"1) 1893", "2) 1902", "3) 1921", "4) 1946"},
        {"1) Pong", "2) Tennis for Two", "3) Space Invaders", "4) Chessmaster"},
        {"1) What", "2) Who", "3) I Don't Know", "4) Today"},
        {"1) Vincent van Gogh", "2) Pablo Picasso", "3) CORRECT", "4) Michelangelo"},
        {"1) Sydney", "2) Melbourne", "3) CORRECT", "4) Brisbane"},
        {"1) Venus", "2) Earth", "3) CORRECT", "4) Mars"},
        {"1) Gold", "2) CORRECT", "3) Iron", "4) Quartz"},
        {"1) Pacific Ocean", "2) CORRECT", "3) Indian Ocean", "4) Arctic Ocean"},
        {"1) 1944", "2) 1945", "3) 1946", "4) CORRECT"},
        {"1) Helium", "2) CORRECT", "3) Oxygen", "4) Carbon"},
        {"1) Amazon River", "2) CORRECT", "3) Yangtze River", "4) Mississippi River"},
        {"1) Star Wars", "2) CORRECT", "3) The Matrix", "4) Lord of the Rings"},
        {"1) CORRECT", "2) Yen", "3) Ringgit", "4) Baht"},
        {"1) Oxygen", "2) Nitrogen", "3) CORRECT", "4) Hydrogen"},
        {"1) Sahara Desert", "2) Gobi Desert", "3) CORRECT", "4) Arabian Desert"},
        {"1) Charles Dickens", "2) CORRECT", "3) Jane Austen", "4) Mark Twain"}
    };

    private static final int[] CORRECT_ANSWER = {2, 3, 1, 3, 1, 2, 2, 3, 3, 3, 2, 4, 2, 2, 2, 1, 2, 3, 3, 2};

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

        for (int i = 0; i < QUESTIONS.length; i++) {
            console.println("\nQuestion " + (i + 1) + ": " + QUESTIONS[i]);
            for (String choice : ANSWER_CHOICES[i]) {
                console.println(choice);
            }

            int answer = getAnswerFromUser(ANSWER_CHOICES[i].length);
            if (answer == CORRECT_ANSWER[i]) {
                console.println("Correct!");
                score++;
            } else {
                console.println("Sorry, the correct answer was " + CORRECT_ANSWER[i] + ".");
            }
        }

        console.println("\nGame over! You scored " + score + " out of " + QUESTIONS.length + ".");
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

