package com.github.zipcodewilmington.casino.games.war;

import java.util.ArrayList;
import java.util.List;

import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.casino.shared.Card;
import com.github.zipcodewilmington.casino.shared.Deck;
import com.github.zipcodewilmington.utils.AnsiColor;
import com.github.zipcodewilmington.utils.IOConsole;

/*
 * Casino War — player draws a card, dealer draws a card, higher rank wins.
 * On a tie, player can surrender (lose half) or go to war (double the bet,
 * burn 6 cards, draw again). Tie-on-war keeps escalating until someone wins.
 */
 
public class WarGame implements GameInterface {

    private PlayerInterface player;
    private Deck deck = new Deck();
    private IOConsole console = new IOConsole(AnsiColor.PURPLE);

    @Override
    public void add(PlayerInterface newPlayer) {
        this.player = newPlayer;
    }

    @Override
    public void remove(PlayerInterface oldPlayer) {
        this.player = null;
    }

    public List<PlayerInterface> getPlayers() {
        List<PlayerInterface> result = new ArrayList<>();
        if (player != null) {
            result.add(player);
        }
        return result;
    }

    @Override
    public void run() {
        if (player == null) {
            console.println("No player at the table.");
            return;
        }

        console.println("Welcome to Casino War! Higher card wins. Aces are high.");

        boolean keepPlaying = true;
        while (keepPlaying) {
            // Refill and reshuffle before each round so we never run out of cards.
            deck.reset();
            deck.shuffle();

            playOneRound();

            String answer = console.getStringInput("Play again? (yes/no)");
            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                keepPlaying = true;
            } else {
                keepPlaying = false;
            }
        }
        console.println("Thanks for playing!");
    }

    private void playOneRound() {
        console.println("Your current balance: $" + player.getCasinoAccount().getBalance());
        long bet = console.getLongInput("How much do you want to bet?");

        if (!player.getCasinoAccount().withdraw(bet)) {
            console.println("Insufficient funds for that bet.");
            return;
        }

        // Each side draws one card.
        Card playerCard = deck.draw();
        Card dealerCard = deck.draw();
        console.println("You drew: " + playerCard);
        console.println("Dealer drew: " + dealerCard);

        // Pull out the rank values so the if/else reads obviously.
        int playerRank = playerCard.getRank().getValue();
        int dealerRank = dealerCard.getRank().getValue();

        if (playerRank > dealerRank) {
            console.println("You win " + bet + "!");
            player.getCasinoAccount().deposit(2 * bet);
        } else if (playerRank < dealerRank) {
            console.println("Dealer wins. You lose " + bet + ".");
            // Bet already withdrawn
        } else {
            // It's a tie — drop into the war loop.
            handleTie(bet);
        }
    }

    private void handleTie(long startingBet) {
        long currentBet = startingBet;
        boolean stillTied = true;

        while (stillTied) {
            console.println("Tie! Current bet: " + currentBet);
            String answer = console.getStringInput(
                "Type 'war' to double down, or 'surrender' to lose half:");

            if (answer.equalsIgnoreCase("surrender")) {
                long loss = currentBet / 2;
                console.println("You surrender. You lose " + loss + ".");
                player.getCasinoAccount().deposit(currentBet / 2);
                return;
            }

            // Going to war: withdraw additional bet to double down.
            if (!player.getCasinoAccount().withdraw(currentBet)) {
                console.println("Insufficient funds for war.");
                return;
            }
            currentBet = currentBet * 2;

            // Burn 6 cards (3 each side).
            for (int i = 0; i < 6; i++) {
                deck.draw();
            }

            Card playerCard = deck.draw();
            Card dealerCard = deck.draw();
            console.println("WAR! You drew: " + playerCard);
            console.println("WAR! Dealer drew: " + dealerCard);

            int playerRank = playerCard.getRank().getValue();
            int dealerRank = dealerCard.getRank().getValue();

            if (playerRank > dealerRank) {
                console.println("You win " + currentBet + "!");
                player.getCasinoAccount().deposit(2 * currentBet);
                stillTied = false;
            } else if (playerRank < dealerRank) {
                console.println("Dealer wins. You lose " + currentBet + ".");
                // Bet already withdrawn
                stillTied = false;
            } else {
                // Another tie — loop back with a doubled bet.
                console.println("ANOTHER TIE! Stakes are escalating...");
            }
        }
    }
}
