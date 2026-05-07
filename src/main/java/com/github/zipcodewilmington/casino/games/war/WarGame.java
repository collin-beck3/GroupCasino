package com.github.zipcodewilmington.casino.games.war;

import java.util.ArrayList;
import java.util.List;

import com.github.zipcodewilmington.casino.CasinoAccount;
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
    private Deck deck;
    private IOConsole console;

    public WarGame() {
        this(new IOConsole(AnsiColor.PURPLE), new Deck());
    }

    public WarGame(IOConsole console, Deck deck) {
        this.console = console;
        this.deck = deck;
    }

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
            deck.reset();
            deck.shuffle();
            playOneRound();
            keepPlaying = askYesNo("Play again? (yes/no)");
        }
        console.println("Thanks for playing!");
    }

    public boolean askYesNo(String prompt) {
        String answer = console.getStringInput(prompt);
        return answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y");
    }

    public long promptForBet(long maxBet) {
        while (true) {
            long bet = console.getLongInput("How much do you want to bet?");
            if (bet <= 0) {
                console.println("Bet must be greater than 0.");
                continue;
            }
            if (bet > maxBet) {
                console.println("Bet cannot exceed your balance of " + maxBet + ".");
                continue;
            }
            return bet;
        }
    }

    public void ensureDeckCanDeal(int cardsNeeded) {
        if (deck.cardsRemaining() < cardsNeeded) {
            deck.reset();
            deck.shuffle();
        }
    }

    public long readBalance(CasinoAccount account) {
        // Stub: return a generous balance for development/testing
        return 1000L;
    }

    public void settle(CasinoAccount account, long delta) {
        if (delta > 0) {
            account.deposit(delta);
            console.println("You win " + delta + "!");
        } else if (delta < 0) {
            account.withdraw(-delta);
            console.println("You lose " + (-delta) + ".");
        }
    }

    private void playOneRound() {
        long balance = readBalance(player.getCasinoAccount());
        console.println("Your current balance: $" + balance);
        
        long bet = promptForBet(balance);
        
        if (!player.getCasinoAccount().withdraw(bet)) {
            console.println("Insufficient funds for that bet.");
            return;
        }

        ensureDeckCanDeal(2);
        Card playerCard = deck.draw();
        Card dealerCard = deck.draw();
        console.println("You drew: " + playerCard);
        console.println("Dealer drew: " + dealerCard);

        int playerRank = playerCard.getRank().getValue();
        int dealerRank = dealerCard.getRank().getValue();

        if (playerRank > dealerRank) {
            settle(player.getCasinoAccount(), bet);
        } else if (playerRank < dealerRank) {
            settle(player.getCasinoAccount(), -bet);
        } else {
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
                settle(player.getCasinoAccount(), -loss);
                return;
            }

            if (!player.getCasinoAccount().withdraw(currentBet)) {
                console.println("Insufficient funds for war.");
                return;
            }
            currentBet = currentBet * 2;

            ensureDeckCanDeal(8);
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
                settle(player.getCasinoAccount(), currentBet);
                stillTied = false;
            } else if (playerRank < dealerRank) {
                settle(player.getCasinoAccount(), -currentBet);
                stillTied = false;
            } else {
                console.println("ANOTHER TIE! Stakes are escalating...");
            }
        }
    }
}
