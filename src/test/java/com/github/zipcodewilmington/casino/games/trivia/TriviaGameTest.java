package com.github.zipcodewilmington.casino.games.trivia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.zipcodewilmington.casino.CasinoAccount;

public class TriviaGameTest {

    private CasinoAccount account;
    private TriviaPlayer player;
    private TriviaGame game;

    @BeforeEach
    public void setUp() {
        // given
        account = new CasinoAccount("TestPlayer", 100.0);
        player  = new TriviaPlayer(account);
        game    = new TriviaGame();
        game.add(player);
    }

    @Test
    public void testGameIsNotNull() {
        assertNotNull(game);
    }

    @Test
    public void testAddPlayer() {
        // given — fresh game with no players
        TriviaGame freshGame = new TriviaGame();
        // when
        freshGame.add(player);
        // then — confirms the exact same player object was added
        assertSame(player, freshGame.getPlayers().get(0));
    }

    @Test
    public void testRemovePlayer() {
        // when
        game.remove(player);
        // then — confirms the exact player object is no longer in the list
        assertFalse(game.getPlayers().contains(player));
    }

    @Test
    public void testAccountSurvivesRemove() {
        // given — confirm same account object before remove
        assertSame(account, player.getCasinoAccount());
        // when
        game.remove(player);
        // then — same account object still exists after remove
        assertSame(account, player.getCasinoAccount());
    }

    @Test
    public void testNonGamblingBalanceUnchanged() {
        // given — trivia is non-gambling, balance must never change
        double before = account.getBalance();
        // when
        game.run();
        // then — same account object has same balance
        assertSame(account, player.getCasinoAccount());
        assertEquals(before, account.getBalance(), 0.01);
    }

    @Test
    public void testRun() {
        assertDoesNotThrow(() -> game.run());
    }
}