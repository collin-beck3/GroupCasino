package com.github.zipcodewilmington.casino.games.war;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.github.zipcodewilmington.CasinoAccount;
import com.github.zipcodewilmington.casino.shared.Deck;
import com.github.zipcodewilmington.utils.AnsiColor;
import com.github.zipcodewilmington.utils.IOConsole;

public class WarGameTest {

    /* Builds a WarGame whose IOConsole reads from the given input string. */
    private WarGame gameWithInput(String stdin) {
        ByteArrayInputStream in = new ByteArrayInputStream(stdin.getBytes());
        PrintStream out = new PrintStream(new ByteArrayOutputStream());
        IOConsole console = new IOConsole(AnsiColor.AUTO, in, out);
        return new WarGame(console, new Deck());
    }

    /* Builds a WarGame whose output is captured so tests can inspect it. */
    private WarGame gameWithCapturedOutput(String stdin, ByteArrayOutputStream captured) {
        ByteArrayInputStream in = new ByteArrayInputStream(stdin.getBytes());
        IOConsole console = new IOConsole(AnsiColor.AUTO, in, new PrintStream(captured));
        return new WarGame(console, new Deck());
    }

    @Test
    public void addAppendsToPlayers() {
        WarGame game = gameWithInput("");
        WarPlayer p = new WarPlayer(new CasinoAccount());
        game.add(p);
        assertEquals(1, game.getPlayers().size());
        assertSame(p, game.getPlayers().get(0));
    }

    @Test
    public void removeTakesPlayerOut() {
        WarGame game = gameWithInput("");
        WarPlayer p = new WarPlayer(new CasinoAccount());
        game.add(p);
        game.remove(p);
        assertTrue(game.getPlayers().isEmpty());
    }

    @Test
    public void runWithNoPlayersExitsImmediately() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        WarGame game = gameWithCapturedOutput("", captured);
        game.run();
        assertTrue(captured.toString().contains("No players"),
                "Should print a 'no players' message and exit");
    }

    @Test
    public void askYesNoReturnsTrueForYes() {
        WarGame game = gameWithInput("yes\n");
        assertTrue(game.askYesNo("Continue?"));
    }

    @Test
    public void askYesNoReturnsTrueForY() {
        WarGame game = gameWithInput("y\n");
        assertTrue(game.askYesNo("Continue?"));
    }

    @Test
    public void askYesNoReturnsFalseForNo() {
        WarGame game = gameWithInput("no\n");
        assertFalse(game.askYesNo("Continue?"));
    }

    @Test
    public void askYesNoReturnsFalseForGarbage() {
        WarGame game = gameWithInput("maybe\n");
        assertFalse(game.askYesNo("Continue?"));
    }

    @Test
    public void promptForBetAcceptsValidBet() {
        WarGame game = gameWithInput("100\n");
        assertEquals(100L, game.promptForBet(500L));
    }

    @Test
    public void promptForBetRejectsZeroAndAsksAgain() {
        // First input is invalid (0), second input is valid.
        WarGame game = gameWithInput("0\n50\n");
        assertEquals(50L, game.promptForBet(500L));
    }

    @Test
    public void promptForBetRejectsOverMaxAndAsksAgain() {
        WarGame game = gameWithInput("9999\n10\n");
        assertEquals(10L, game.promptForBet(500L));
    }

    @Test
    public void ensureDeckCanDealReshufflesWhenLow() {
        Deck deck = new Deck();
        // Drain to 5 cards remaining.
        for (int i = 0; i < 47; i++) deck.draw();
        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
        IOConsole console = new IOConsole(AnsiColor.AUTO, in, new PrintStream(new ByteArrayOutputStream()));
        WarGame game = new WarGame(console, deck);

        game.ensureDeckCanDeal(10);
        assertEquals(52, deck.cardsRemaining(), "Deck should have been reset and reshuffled");
    }

    @Test
    public void ensureDeckCanDealLeavesAloneWhenEnough() {
        Deck deck = new Deck();
        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
        IOConsole console = new IOConsole(AnsiColor.AUTO, in, new PrintStream(new ByteArrayOutputStream()));
        WarGame game = new WarGame(console, deck);

        game.ensureDeckCanDeal(10);
        assertEquals(52, deck.cardsRemaining(), "Full deck should not be touched");
    }

    @Test
    public void readBalanceReturnsStubWhileCasinoAccountUnwired() {
        // Until Dev E wires CasinoAccount.getBalance(), readBalance() returns
        // the stub balance so War can be played end-to-end during development.
        WarGame game = gameWithInput("");
        long balance = game.readBalance(new CasinoAccount());
        assertTrue(balance > 0, "Stub balance must be positive so bets can happen");
    }

    @Test
    public void settleDoesNotThrowOnStub() {
        // Stubbed settle just prints — should never throw, even on negative deltas.
        WarGame game = gameWithInput("");
        CasinoAccount account = new CasinoAccount();
        assertDoesNotThrow(() -> game.settle(account, 100L));
        assertDoesNotThrow(() -> game.settle(account, -100L));
        assertDoesNotThrow(() -> game.settle(account, 0L));
    }
}
