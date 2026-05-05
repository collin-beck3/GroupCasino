package com.github.zipcodewilmington.casino.games.dice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.zipcodewilmington.casino.CasinoAccount;

public class DiceTest {

    private CasinoAccount account;
    private DicePlayer player;
    private DiceGame game;

    @BeforeEach
    public void setUp() {
        // given
        account = new CasinoAccount("TestPlayer", 200.0);
        player = new DicePlayer(account);
        game = new DiceGame(player);
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
    public void testRollReturnsValidRange() {
        // given
        game.startGame();
        // when
        int result = game.roll();
        // then - 2 dice, range 2 to 12
        assertTrue(result >= 2 && result <= 12);
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
