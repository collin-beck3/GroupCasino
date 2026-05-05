package com.github.zipcodewilmington.casino.games.roulette;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;

import java.util.Scanner;

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
            return "Invalid bet or not enough balance.";
        }

        System.out.print("Pick a number between 0 and 36: ");
        int guess = scanner.nextInt();

        int winningNumber = (int) (Math.random() * 37);

        System.out.println("Winning number: " + winningNumber);

        if (guess == winningNumber) {
            double winnings = bet * 36;
            casinoAccount.deposit(winnings);
            return "You won $" + winnings;
        }

        return "You lost $" + bet;
    }
}