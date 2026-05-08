package com.github.zipcodewilmington.casino.games.slots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.utils.IOConsole;

/**
 * Created by leon on 7/21/2020.
 */
public class SlotsGame implements GameInterface { 
    private int bet;
    private double multiplier;
    Random random = new Random();
    private List<PlayerInterface> players;
    private boolean gameLoop;
    private List<String> logos;
    private Map<String, Double> threeMatchPayout;
    private Map<String, Double> twoMatchPayout;

    public SlotsGame() {
        gameLoop = false;
        players = new ArrayList<>();
        logos = new ArrayList<>();
        logos.add("♥️ ");
        logos.add("♥️ ");
        logos.add("♥️ ");
        logos.add("♥️ ");
        logos.add("♦️ ");
        logos.add("♦️ ");
        logos.add("♦️ ");
        logos.add("♦️ ");
        logos.add("♣️ ");
        logos.add("♣️ ");
        logos.add("♣️ ");
        logos.add("♣️ ");
        logos.add("♠️ ");
        logos.add("♠️ ");
        logos.add("♠️ ");
        logos.add("♠️ ");
        logos.add("🎲");
        logos.add("🎲");
        logos.add("🎲");
        logos.add("🔔");
        logos.add("🔔");
        logos.add("🔔");
        logos.add("💰");
        logos.add("💰");
        logos.add("7");
        logos.add("💎");

        threeMatchPayout = new HashMap<>();
        threeMatchPayout.put("💎", 12.0);
        threeMatchPayout.put("7", 8.0);
        threeMatchPayout.put("💰", 6.0);
        threeMatchPayout.put("🔔", 4.0);
        threeMatchPayout.put("🎲", 4.0);
        threeMatchPayout.put("♠️ ", 3.0);
        threeMatchPayout.put("♥️ ", 3.0);
        threeMatchPayout.put("♦️ ", 3.0);
        threeMatchPayout.put("♣️ ", 3.0);

        twoMatchPayout = new HashMap<>();
        twoMatchPayout.put("💎", 5.0);
        twoMatchPayout.put("7", 4.0);
        twoMatchPayout.put("💰", 3.0);
        twoMatchPayout.put("🔔", 2.5);
        twoMatchPayout.put("🎲", 2.5);
        twoMatchPayout.put("♠️ ", 2.0);
        twoMatchPayout.put("♥️ ", 2.0);
        twoMatchPayout.put("♦️ ", 2.0);
        twoMatchPayout.put("♣️ ", 2.0);
    }

    @Override
    public void run() {
        printGameStart();
        while(true) {
            multiplier = 0;
            PlayerInterface player = players.get(0);
            CasinoAccount account = player.getCasinoAccount();
            System.out.println("Your current balance: " + account.getBalance());
            IOConsole input = new IOConsole();
            while(true) {
                bet = input.getIntegerInput("Enter your bet: ");
                if (bet <= 0) {
                    System.out.println("Bet must be greater than 0.");
                    continue;
                }
                if (bet > account.getBalance()) {
                    System.out.println("Insufficient balance.");
                    continue;
                }
                break; 
            }
            account.withdraw(bet);
            String spin1 = logos.get(random.nextInt(logos.size()));
            String spin2 = logos.get(random.nextInt(logos.size()));
            String spin3 = logos.get(random.nextInt(logos.size()));
            System.out.println("["+ spin1 + "] | [" + spin2 + "] | [" + spin3 + "]");
            if(spin1.equals(spin2) && spin2.equals(spin3)){
                multiplier = threeMatchPayout.getOrDefault(spin1, 0.0);
            } else if (spin1.equals(spin2)) {
                multiplier = twoMatchPayout.getOrDefault(spin1, 0.0);
            }
            else if (spin2.equals(spin3)) {
                multiplier = twoMatchPayout.getOrDefault(spin2, 0.0);
            }
            else if (spin1.equals(spin3)) {
                multiplier = twoMatchPayout.getOrDefault(spin1, 0.0);
            }

            if(multiplier > 0) {
                double winnings = bet * multiplier;
                System.out.println("Congratulations you have won: " + winnings);
                account.deposit(winnings);
            } else {
                System.out.println("You didn't win anything :(");
            }

            if(player.getCasinoAccount().getBalance() <= 0) {
                System.out.println("You are out of money! Game over.");
                return;
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
        System.out.println("Welcome to the Slots Game!");
    }

    @Override
    public void add(PlayerInterface player) {
        players.add(player);
    }

    @Override
    public void remove(PlayerInterface player) {
        players.remove(player);
    }

    public List<PlayerInterface> getPlayers() {
        return players;
    }

}
