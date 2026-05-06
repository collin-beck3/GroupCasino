package com.github.zipcodewilmington.casino.games.numberguess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.utils.IOConsole;

public class NumberGuessGame implements GameInterface {

    private int number;
    private int turn;
    private static final int maxTurn = 6;
    private List<PlayerInterface> players;
    private boolean gameWon;
    Random random = new Random();
    IOConsole input = new IOConsole();

    public NumberGuessGame() {
        this.number = random.nextInt(100) + 1;
        this.turn = 0;
        gameWon = false;
        players = new ArrayList<>();
    }

    public void run() {
        if(players.isEmpty()) {
                System.out.println("At least 1 player needed.");
                return;
            }
        while(true) {
            turn = 0;
            number = random.nextInt(100) + 1;
            gameWon = false;
            int startIndex = random.nextInt(players.size());
            printGameStart();
            while (turn < maxTurn && !gameWon) {
                for(int i = 0; i < players.size(); i++) {
                    int actualIndex = (startIndex + i) % players.size();
                    PlayerInterface player = players.get(actualIndex);
                    System.out.println("Player: " + player.getCasinoAccount().getPlayerName() + ", it's your turn.");
                    int guess = player.play();
                    turn++;
                    if(guess == number){
                        System.out.println("Congratulation " + player.player.getArcadeAccount().getAccountName() + " wins!");
                        gameWon = true;
                        break;
                    }
                    if(guess < number) {
                        System.out.println("Too Low!");
                    } else {
                        System.out.println("Too High!");
                    }
                    if (turn >= maxTurn) {
                        break;
                    }
                }
            }
            if(!gameWon) {
                System.out.println("Game Over.");
                System.out.println("The number was " + number);
            }

            while (true) {
                String playAgain = input.getStringInput("Do you want to play again? (Y/N)");

                if (playAgain.equalsIgnoreCase("N") || playAgain.equalsIgnoreCase("NO")) {
                    return;
                } else if (playAgain.equalsIgnoreCase("Y") || playAgain.equalsIgnoreCase("YES")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter Y or N.");
                }
            }
        }
    }

    public void printGameStart() {
        System.out.println("Welcome to the Number Guess Game! A number between 1 and 100 is randomly chosen. Can you guess it?");
        System.out.println("You have " + maxTurn + " total turns. Good luck!");
    }

    @Override
    public void add(PlayerInterface player) {
        if(players.size() < 2) {
            players.add(player);
        }
    }

    @Override
    public void remove(PlayerInterface player) {
        players.remove(player);
    }

}