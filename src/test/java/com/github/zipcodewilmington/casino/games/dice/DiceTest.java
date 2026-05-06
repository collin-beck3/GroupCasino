package com.github.zipcodewilmington.casino.games.dice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
        player  = new DicePlayer(account);
        game    = new DiceGame();       // no arguments
        game.add(player);               // add player separately
    }

    @Test
    public void testGameIsNotNull() {
        assertNotNull(game);
    }

    @Test
    public void testAddPlayer() {
        // when
        game.add(player);
        // then
        assertFalse(game.getPlayers().isEmpty());
    }

    @Test
    public void testRemovePlayer() {
        // given
        game.add(player);
        // when
        game.remove(player);
        // then
        assertTrue(game.getPlayers().isEmpty());
    }

    @Test
    public void testAccountSurvivesRemove() {
        // given
        game.add(player);
        // when
        game.remove(player);
        // then
        assertNotNull(account);
    }

    @Test
    public void testRun() {
        // given
        game.add(player);
        // when
        game.run();
        // then
        assertNotNull(game);
    }

}
