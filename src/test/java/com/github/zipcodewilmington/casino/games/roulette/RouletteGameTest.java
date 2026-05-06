package com.github.zipcodewilmington.casino.games.roulette;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.zipcodewilmington.CasinoAccount;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouletteGameTest {

    private CasinoAccount account;
    private RoulettePlayer player;
    private RouletteGame game;

   @BeforeEach
    public void setUp() {
        // given
        account = new CasinoAccount("TestPlayer", 300.0);
        player  = new RoulettePlayer(account);
        game    = new RouletteGame();   // no arguments
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
