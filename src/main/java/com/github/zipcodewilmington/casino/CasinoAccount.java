package com.github.zipcodewilmington.casino;

/**
 * Created by leon on 7/21/2020.
 * `ArcadeAccount` is registered for each user of the `Arcade`.
 * The `ArcadeAccount` is used to log into the system to select a `Game` to play.
 */

public class CasinoAccount {

    private String playerName;
    private double balance;

    public CasinoAccount(String playerName, double startingBalance) {
        this.playerName = playerName;
        this.balance = startingBalance;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true; 
        }
        return false;
    }

    @Override
    public String toString() {
        return playerName + " | Balance: $" + String.format("%.2f", balance);
    }
}
