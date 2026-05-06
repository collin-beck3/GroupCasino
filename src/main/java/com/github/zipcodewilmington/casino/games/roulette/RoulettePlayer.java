package com.github.zipcodewilmington.casino.games.roulette;

import java.util.Scanner;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;

public class RoulettePlayer implements PlayerInterface {
    private CasinoAccount casinoAccount;
    private Scanner scanner = new Scanner(System.in);

    public RoulettePlayer(CasinoAccount casinoAccount) {
        this.casinoAccount = casinoAccount;
    }

    @Override
    public CasinoAccount getCasinoAccount() {
        return casinoAccount;
    }

    @Override
    public String play() {
        System.out.println("=== Roulette Game ===");
        System.out.println("Balance: $" + casinoAccount.getBalance());

        System.out.print("Enter bet amount: ");
        double bet = scanner.nextDouble();

        if (!casinoAccount.withdraw(bet)) {
            return "Invalid bet or insufficient balance.";
        }

        // User picks number
        System.out.print("Pick a number (0–36): ");
        int chosenNumber = scanner.nextInt();

        // User picks color
        System.out.print("Pick color (red/black): ");
        String chosenColor = scanner.next().toLowerCase();

        // Spin roulette
        int winningNumber = (int) (Math.random() * 37);

        // Determine color
        String winningColor;
        if (winningNumber == 0) {
            winningColor = "green";
        } else if (winningNumber % 2 == 0) {
            winningColor = "black";
        } else {
            winningColor = "red";
        }

        System.out.println("🎯 Result: " + winningNumber + " " + winningColor);

        // Check win
        if (chosenNumber == winningNumber) {
            double winnings = bet * 36;
            casinoAccount.deposit(winnings);
            return "🔥 JACKPOT! You won $" + winnings;
        }

        if (chosenColor.equals(winningColor)) {
            double winnings = bet * 2;
            casinoAccount.deposit(winnings);
            return "✅ Color match! You won $" + winnings;
        }

        return "❌ You lost $" + bet;
    }
}