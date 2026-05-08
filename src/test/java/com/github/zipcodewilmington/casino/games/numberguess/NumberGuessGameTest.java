package com.github.zipcodewilmington.casino.games.numberguess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        game    = new NumberGuessGame();
        game.add(player);
    }

    @Test
    public void testGameIsNotNull() {
        assertNotNull(game);
    }

    //@Test
    //public void testAddPlayer() {
        //NumberGuessGame freshGame = new NumberGuessGame();
        //freshGame.add(player);
        // assertSame(player, freshGame.getPlayers().get(0));
        // TODO — uncomment when Dev 2 adds getPlayers()
    //}

    @Test
    public void testAddPlayer() {
        // given — fresh game with no players
        NumberGuessGame freshGame = new NumberGuessGame();
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
    public void testNoBalanceChangeOnPlay() {
        // given — non-gambling game, balance must never change
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