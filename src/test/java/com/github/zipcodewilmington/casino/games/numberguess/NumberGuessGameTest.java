package com.github.zipcodewilmington.casino.games.numberguess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.zipcodewilmington.casino.CasinoAccount;

public class NumberGuessGameTest {

    private CasinoAccount account;
    private NumberGuessPlayer player;
    private NumberGuessGame game;

    @BeforeEach
    public void setUp() {
        // given
        account = new CasinoAccount("TestPlayer", 100.0);
        player  = new NumberGuessPlayer(account);
        game    = new NumberGuessGame(player);
    }

    @Test
    public void testGameIsNotNull() {
        assertNotNull(game);
    }

    @Test
    public void testStartGame() {
        // when
        game.startGame();
        // then
        assertNotNull(game.getPlayer());
    }

    @Test
    public void testEndGameNullsOutPlayer() {
        // given
        game.startGame();
        // when
        game.endGame();
        // then
        assertNull(game.getPlayer());
    }

    @Test
    public void testGuessReturnsValidResponse() {
        // given
        game.startGame();
        // when
        String result = game.guess(50);
        // then
        assertTrue(
            result.equals("Too high!") ||
            result.equals("Too low!")  ||
            result.equals("Correct!")
        );
    }

    @Test
    public void testNoBalanceChangeOnGuess() {
        // given — non-gambling game, balance must never change
        game.startGame();
        double before = account.getBalance();
        // when
        game.guess(50);
        // then
        assertEquals(before, account.getBalance(), 0.01);
    }

    @Test
    public void testAccountSurvivesEndGame() {
        // given
        game.startGame();
        // when
        game.endGame();
        // then
        assertNotNull(account);
    }
}
