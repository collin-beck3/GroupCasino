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
 * Casino War — place ONE bet before play starts, then watch the whole deck
 * play out automatically.
 *
 * Each round: player card vs dealer card.  Higher rank wins the round.
 * On a tie:   6 cards are burned face-down, then one war card each is
 *             drawn — winner takes the round.  A war tie is a push.
 *
 * Final payout (after all 52 cards are spent):
 *   player rounds > dealer rounds → WIN (double the bet)
 *   dealer rounds > player rounds → LOSE
 *   equal                         → PUSH (bet returned)
 */
public class WarGame implements GameInterface {

    /** Milliseconds to pause between normal rounds (change to taste). */
    private static final long ROUND_DELAY_MS = 1200;

    /** Extra pause used during war sequences for dramatic effect. */
    private static final long WAR_DELAY_MS   = 800;

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

    // ── GameInterface ──────────────────────────────────────────────────────────

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
        if (player != null) result.add(player);
        return result;
    }

    // ── Main entry point ───────────────────────────────────────────────────────

    @Override
    public void run() {
        if (player == null) {
            console.println("No players at the table.");
            return;
        }

        console.println("===========================================");
        console.println("         W E L C O M E   T O             ");
        console.println("           C A S I N O   W A R           ");
        console.println("  Place your bet, then watch it unfold.   ");
        console.println("===========================================");

        boolean keepPlaying = true;
        while (keepPlaying) {
            deck.reset();
            deck.shuffle();
            playFullGame();
            keepPlaying = askYesNo("Play again? (yes/no)");
        }
        console.println("Thanks for playing Casino War!");
    }

    // ── Core game loop ─────────────────────────────────────────────────────────

    /**
     * Deals the entire shuffled deck in player/dealer pairs.
     * The player bets once before any cards are revealed.
     */
    private void playFullGame() {
        long balance = readBalance(player.getCasinoAccount());
        console.println("\nYour current balance: $" + balance);

        long bet = promptForBet(balance);
        if (!player.getCasinoAccount().withdraw(bet)) {
            console.println("Insufficient funds — bet not placed.");
            return;
        }

        console.println("\n*** LET THE WAR BEGIN — dealing through the deck... ***\n");

        int playerWins = 0;
        int dealerWins = 0;
        int roundNum   = 0;

        while (deck.cardsRemaining() >= 2) {
            roundNum++;
            Card playerCard = deck.draw();
            Card dealerCard = deck.draw();

            console.println("------ Round " + roundNum
                    + "  [" + deck.cardsRemaining() + " cards left] ------");
            printCardComparison(playerCard, dealerCard);

            int cmp = playerCard.compareTo(dealerCard);
            if (cmp > 0) {
                console.println("  >> YOU win round " + roundNum + "!\n");
                playerWins++;
                pause(ROUND_DELAY_MS);
            } else if (cmp < 0) {
                console.println("  >> Dealer wins round " + roundNum + ".\n");
                dealerWins++;
                pause(ROUND_DELAY_MS);
            } else {
                // ── WAR ───────────────────────────────────────────────────────
                console.println("  *** TIE — WAR! ***");
                pause(WAR_DELAY_MS); // extra drama before burning cards
                if (deck.cardsRemaining() >= 8) {
                    console.println("  [Burning 6 cards face-down...]");
                    pause(WAR_DELAY_MS);
                    for (int i = 0; i < 6; i++) deck.draw();

                    Card warPlayer = deck.draw();
                    Card warDealer = deck.draw();
                    console.println("  WAR CARDS:");
                    printCardComparison(warPlayer, warDealer);

                    int warCmp = warPlayer.compareTo(warDealer);
                    if (warCmp > 0) {
                        console.println("  >> YOU win the war!\n");
                        playerWins++;
                    } else if (warCmp < 0) {
                        console.println("  >> Dealer wins the war.\n");
                        dealerWins++;
                    } else {
                        console.println("  >> Double tie — PUSH!\n");
                    }
                } else {
                    console.println("  [Not enough cards for war — PUSH!]\n");
                }
                pause(ROUND_DELAY_MS);
            }
        }

        // ── Final tally & payout ───────────────────────────────────────────────
        console.println("===========================================");
        console.println("            F I N A L   S C O R E        ");
        console.println("  Rounds played : " + roundNum);
        console.println("  Your wins     : " + playerWins);
        console.println("  Dealer wins   : " + dealerWins);
        console.println("===========================================");

        if (playerWins > dealerWins) {
            player.getCasinoAccount().deposit(bet * 2); // return stake + profit
            console.println("  YOU WIN!  +$" + bet + " profit!");
        } else if (dealerWins > playerWins) {
            // bet already withdrawn — nothing more to do
            console.println("  Dealer wins. You lose $" + bet + ".");
        } else {
            player.getCasinoAccount().deposit(bet); // return stake, no profit
            console.println("  PUSH — your $" + bet + " bet is returned.");
        }
        console.println("  New balance: $"
                + (long) player.getCasinoAccount().getBalance() + "\n");
    }

    // ── Display helpers ────────────────────────────────────────────────────────

    /**
     * Prints two cards side-by-side with "You" / "Dealer" labels underneath.
     *
     * <pre>
     * .---------. .---------.
     * | 7       | | Q       |
     * |         | |         |
     * |    ♦    | |    ♠    |
     * |         | |         |
     * |       7 | |       Q |
     * '---------' '---------'
     *     You         Dealer
     * </pre>
     */
    private void printCardComparison(Card playerCard, Card dealerCard) {
        String[] p = playerCard.toAsciiArt();
        String[] d = dealerCard.toAsciiArt();
        for (int i = 0; i < p.length; i++) {
            console.println("  " + p[i] + " " + d[i]);
        }
        // Labels: each card art is 11 chars wide, 1-char gap between them
        console.println("      You             Dealer");
    }

    // ── Utility methods (kept public so existing tests still compile) ──────────

    public boolean askYesNo(String prompt) {
        String answer = console.getStringInput(prompt);
        return answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y");
    }

    public long promptForBet(long maxBet) {
        while (true) {
            long bet = console.getLongInput("How much do you want to bet? (max $" + maxBet + ")");
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

    /**
     * Sleeps for the given number of milliseconds, swallowing any
     * InterruptedException so callers don't need to declare it.
     */
    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupted status
        }
    }

    /** Stub: returns a generous balance for development/testing. */
    public long readBalance(CasinoAccount account) {
        return 1000L;
    }

    /**
     * Applies a signed delta to the player's account and prints a message.
     * Kept for backward compatibility with existing tests and any tie-in code
     * that calls it directly.
     */
    public void settle(CasinoAccount account, long delta) {
        if (delta > 0) {
            account.deposit(delta);
            console.println("You win " + delta + "!");
        } else if (delta < 0) {
            account.withdraw(-delta);
            console.println("You lose " + (-delta) + ".");
        }
    }
}
