package com.github.zipcodewilmington;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.CasinoAccountManager;
import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.casino.games.numberguess.NumberGuessGame;
import com.github.zipcodewilmington.casino.games.numberguess.NumberGuessPlayer;
import com.github.zipcodewilmington.casino.games.slots.SlotsGame;
import com.github.zipcodewilmington.casino.games.slots.SlotsPlayer;
import com.github.zipcodewilmington.casino.games.war.WarGame;
import com.github.zipcodewilmington.casino.games.war.WarPlayer;


public class Casino implements Runnable {

    private final ConsoleUI consoleUI;
    private final CasinoAccountManager casinoAccountManager;

    public Casino(ConsoleUI consoleUI) {
        this.consoleUI = consoleUI;
        this.casinoAccountManager = new CasinoAccountManager();
    }

    @Override
    public void run() {
        String arcadeDashBoardInput;

        do {
            arcadeDashBoardInput = consoleUI.getArcadeDashboardInput();

            if ("select-game".equalsIgnoreCase(arcadeDashBoardInput)) {
                selectGame();
            } else if ("create-account".equalsIgnoreCase(arcadeDashBoardInput)) {
                createAccount();
            }

        } while (!"logout".equalsIgnoreCase(arcadeDashBoardInput));
    }

    private void selectGame() {
        String accountName = consoleUI.getAccountName();
        String accountPassword = consoleUI.getAccountPassword();

        CasinoAccount casinoAccount = casinoAccountManager.getAccount(accountName, accountPassword);

        if (casinoAccount == null) {
            String errorMessage = "No account found with name of [ %s ] and password of [ %s ]";
            throw new RuntimeException(String.format(errorMessage, accountName, accountPassword));
        }

        String gameSelectionInput = consoleUI.getGameSelectionInput().toUpperCase();

        if (gameSelectionInput.equals("SLOTS")) {
            play(new SlotsGame(), new SlotsPlayer());                               //eventually will implement GameInterface and fix this error
        } else if (gameSelectionInput.equals("NUMBERGUESS")) {
            play(new NumberGuessGame(), new NumberGuessPlayer());                   //same as above ^
        } else if (gameSelectionInput.equals("WAR")) {
            play(new WarGame(), new WarPlayer());
        } else {
            String errorMessage = "[ %s ] is an invalid game selection";
            throw new RuntimeException(String.format(errorMessage, gameSelectionInput));
        }
    }

    private void createAccount() {
        consoleUI.println("Welcome to the account-creation screen.");

        String accountName = consoleUI.getAccountName();
        String accountPassword = consoleUI.getAccountPassword();

        CasinoAccount newAccount = casinoAccountManager.createAccount(accountName, accountPassword);
        casinoAccountManager.registerAccount(newAccount);
    }

    private void play(GameInterface game, PlayerInterface player) {
        game.add(player);
        game.run();
    }
}
