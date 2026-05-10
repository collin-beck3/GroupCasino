package com.github.zipcodewilmington;

import com.github.zipcodewilmington.utils.AnsiColor;
import com.github.zipcodewilmington.utils.IOConsole;

public class ConsoleUI {
    private final IOConsole console = new IOConsole(AnsiColor.PURPLE);

    public String getArcadeDashboardInput() {
        return console.getStringInput(new StringBuilder()
                .append("\n Welcome to the GroupCasino Dashboard!")
                .append("\n From here, you can select any of the following options:")
                .append("\n\t[ create-account ], [ select-game ], [ logout ]")
                .toString());
    }

    public int getGameSelectionInput() {
        String prompt = new StringBuilder()
                .append("Welcome to the Game Selection Dashboard!")
                .append("\nFrom here, please select any of the following options to play: ")
                .append("\n\t[ 1 ] Slots")
                .append("\n\t[ 2 ] Number Guess")
                .append("\n\t[ 3 ] War")
                .append("\n\t[ 4 ] Roulette")
                .append("\n\t[ 5 ] Dice / Craps")
                .append("\n\t[ 6 ] Trivia") 
                .append("\n\t[ 0 ] Back to Main Menu")
                .append("\nEnter your selection (0-6): ")
                .toString();
        return console.getIntegerInput(prompt);
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