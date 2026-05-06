package com.github.zipcodewilmington;

import com.github.zipcodewilmington.utils.AnsiColor;
import com.github.zipcodewilmington.utils.IOConsole;

public class ConsoleUI {
    private final IOConsole console = new IOConsole(AnsiColor.BLUE);

    public String getArcadeDashboardInput() {
        return console.getStringInput(new StringBuilder()
                .append("Welcome to the Arcade Dashboard!")
                .append("\nFrom here, you can select any of the following options:")
                .append("\n\t[ create-account ], [ select-game ], [ logout ]")
                .toString());
    }

    public String getGameSelectionInput() {
        return console.getStringInput(new StringBuilder()
                .append("Welcome to the Game Selection Dashboard!")
                .append("\nFrom here, you can select any of the following options:")
                .append("\n\t[ SLOTS ], [ NUMBERGUESS ], [ War ]")
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