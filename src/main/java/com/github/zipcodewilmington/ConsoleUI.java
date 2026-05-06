package com.github.zipcodewilmington;

import com.github.zipcodewilmington.utils.AnsiColor;
import com.github.zipcodewilmington.utils.IOConsole;

public class ConsoleUI {
    private final IOConsole console = new IOConsole(AnsiColor.PURPLE);

    public String getArcadeDashboardInput() {
        return console.getStringInput(new StringBuilder()
                .append("Welcome to the GroupCasino Dashboard!")
                .append("\nFrom here, you can select any of the following options:")
                .append("\n\t[ create-account ], [ select-game ], [ logout ]")
                .toString());
    }

    public String getGameSelectionInput() {
        return console.getStringInput(new StringBuilder()
                .append("Welcome to the Game Selection Dashboard!")
                .append("\nFrom here, please select any of the following options to play:")
                .append("\n\t[ SLOTS ], [ NUMBERGUESS ], [ War ], [Roulette], [Trivia], [Dice / Craps]")
                .toString());
    }

    public String getAccountName() {
        return console.getStringInput("Enter your account name:");
    }

    public String getAccountPassword() {
        return console.getStringInput("Enter your account password:");
    }

    public void println(String message) {
        console.println(message);
    }
}