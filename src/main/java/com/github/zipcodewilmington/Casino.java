package com.github.zipcodewilmington;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.casino.games.dice.DiceGame;
import com.github.zipcodewilmington.casino.games.dice.DicePlayer;
import com.github.zipcodewilmington.casino.games.numberguess.NumberGuessGame;
import com.github.zipcodewilmington.casino.games.numberguess.NumberGuessPlayer;
import com.github.zipcodewilmington.casino.games.roulette.RouletteGame;
import com.github.zipcodewilmington.casino.games.roulette.RoulettePlayer;
import com.github.zipcodewilmington.casino.games.slots.SlotsGame;
import com.github.zipcodewilmington.casino.games.slots.SlotsPlayer;
import com.github.zipcodewilmington.casino.games.trivia.TriviaGame;
import com.github.zipcodewilmington.casino.games.trivia.TriviaPlayer;
import com.github.zipcodewilmington.casino.games.war.WarGame;
import com.github.zipcodewilmington.casino.games.war.WarPlayer;


public class Casino implements Runnable {

    private final ConsoleUI consoleUI;
    private final CasinoAccountManager casinoAccountManager;
    private CasinoAccount currentAccount; 

    public Casino(ConsoleUI consoleUI) {
        this.consoleUI = consoleUI;
        this.casinoAccountManager = new CasinoAccountManager();
    }

    @Override
    public void run() {
        String arcadeDashBoardInput;

        do {
            arcadeDashBoardInput = consoleUI.getArcadeDashboardInput().trim();

            if ("select-game".equalsIgnoreCase(arcadeDashBoardInput)) {
                selectGame();
            } else if ("create-account".equalsIgnoreCase(arcadeDashBoardInput)) {
                createAccount();
            } else if (!"logout".equalsIgnoreCase(arcadeDashBoardInput)) {
                //consoleUI.println("Invalid option. Please choose create-account, select-game, or logout."); 
                currentAccount = null; 
            }

        } while (!"logout".equalsIgnoreCase(arcadeDashBoardInput));
    }

    private void selectGame() {
        if (currentAccount == null) {
            login();
        }

        if (currentAccount == null) { 
            consoleUI.println("Login failed.");
            return; 
        }
        /*String accountName = consoleUI.getAccountName();
        String accountPassword = consoleUI.getAccountPassword();

        CasinoAccount casinoAccount = casinoAccountManager.getAccount(accountName, accountPassword);

        if (casinoAccount == null) {
            consoleUI.println("No account found with that name and password. "); 
            return; */
        //}

        String gameSelectionInput = consoleUI.getGameSelectionInput().trim().toUpperCase();

        switch (gameSelectionInput) {
         case "SLOTS": 
           play(new SlotsGame(), new SlotsPlayer(currentAccount));    
           break;                           
         case "NUMBERGUESS":
            play(new NumberGuessGame(), new NumberGuessPlayer(currentAccount)); 
            break;                 
         case "WAR":
            play(new WarGame(), new WarPlayer(currentAccount));
            break;
         case "ROULETTE":
            play(new RouletteGame(), new RoulettePlayer(currentAccount));
            break;
         case "DICE":
         case "CRAPS":
            play(new DiceGame(), new DicePlayer(currentAccount)); 
            break;
         case "TRIVIA":
            play(new TriviaGame(), new TriviaPlayer(currentAccount));
            break;
        default: 
            consoleUI.println("[ " + gameSelectionInput + " ] is an invalid game selection.");
            //String errorMessage = "[ %s ] is an invalid game selection";
            //throw new RuntimeException(String.format(errorMessage, gameSelectionInput));
        }
        
    }

    private void createAccount() {
        consoleUI.println("Welcome to the account-creation screen.");

        String accountName = consoleUI.getAccountName();
        String accountPassword = consoleUI.getAccountPassword();

        CasinoAccount newAccount = casinoAccountManager.createAccount(accountName, accountPassword);
        casinoAccountManager.registerAccount(newAccount);

        consoleUI.println("Account created successfully!"); 
    }

    private void play(GameInterface game, PlayerInterface player) {

        game.add(player);
        game.run();
        game.remove(player); 
    }

    private void login() { 
        String accountName = consoleUI.getAccountName(); 
        String accountPassword = consoleUI.getAccountPassword();

        currentAccount = casinoAccountManager.getAccount(accountName, accountPassword);
    }
}
