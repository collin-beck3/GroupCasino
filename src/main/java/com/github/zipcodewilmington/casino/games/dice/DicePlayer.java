package com.github.zipcodewilmington.casino.games.dice;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;

import java.util.Scanner;

public class DicePlayer implements PlayerInterface {
    private CasinoAccount casinoAccount;
    private Scanner scanner = new Scanner(System.in);

    public DicePlayer(CasinoAccount casinoAccount) {
        this.casinoAccount = casinoAccount;
    }

    @Override
    public CasinoAccount getCasinoAccount() {
        return casinoAccount;
    }

    @Override
    public String play() {
        System.out.println("=== Dice Game ===");
        System.out.println("Balance: $" + casinoAccount.getBalance());

        System.out.print("Enter bet amount: ");
        double bet = scanner.nextDouble();

        if (!casinoAccount.withdraw(bet)) {
            return "Invalid bet or not enough balance.";
        }

        System.out.print("Guess dice total from 2 to 12: ");
        int guess = scanner.nextInt();

        int dice1 = (int) (Math.random() * 6) + 1;
        int dice2 = (int) (Math.random() * 6) + 1;
        int total = dice1 + dice2;

        System.out.println("Dice 1: " + dice1);
        System.out.println("Dice 2: " + dice2);
        System.out.println("Total: " + total);

        if (guess == total) {
            double winnings = bet * 6;
            casinoAccount.deposit(winnings);
            return "You won $" + winnings;
        }

        return "You lost $" + bet;
    }
}

package com.github.zipcodewilmington.casino.games.dice;

import java.util.Scanner;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;

public class DicePlayer implements PlayerInterface {
    private CasinoAccount casinoAccount;
    private Scanner scanner = new Scanner(System.in);

    public DicePlayer(CasinoAccount casinoAccount) {
        this.casinoAccount = casinoAccount;
    }

    @Override
    public CasinoAccount getCasinoAccount() {
        return casinoAccount;
    }

    @Override
    public String play() {
        System.out.println("=== Dice Game ===");
        System.out.println("Balance: $" + casinoAccount.getBalance());

        System.out.print("Enter bet amount: ");
        double bet = scanner.nextDouble();

        if (!casinoAccount.withdraw(bet)) {
            return "Invalid bet or insufficient balance.";
        }

        // Roll dice
        int dice1 = (int) (Math.random() * 6) + 1;
        int dice2 = (int) (Math.random() * 6) + 1;
        int total = dice1 + dice2;

        System.out.println("🎲 Dice rolled: " + dice1 + " + " + dice2 + " = " + total);

        // Win conditions
        if (total == 7 || total == 11) {
            double winnings = bet * 2;
            casinoAccount.deposit(winnings);
            return "🎉 You WIN! You get $" + winnings;
        }

        // Lose conditions
        if (total == 2 || total == 3 || total == 12) {
            return "💀 You LOSE! Lost $" + bet;
        }

        // Neutral (no rule defined)
        return "😐 No win/lose rule. Bet returned.";
    }
}