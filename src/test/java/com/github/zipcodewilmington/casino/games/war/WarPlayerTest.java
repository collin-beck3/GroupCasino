package com.github.zipcodewilmington.casino.games.war;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.shared.Card;

public class WarPlayerTest {

    private CasinoAccount account;
    private WarPlayer player;

    @BeforeEach
    public void setUp() {
        // given
        account = new CasinoAccount("TestPlayer", 100.0);
        player  = new WarPlayer(account);
    }

    // ── PlayerInterface contract tests ──────────────────

    @Test
    public void testPlayerIsNotNull() {
        assertNotNull(player);
    }

    @Test
    public void testGetAccountReturnsSameObject() {
        // assertSame proves it's the exact same object, not just equal
        assertSame(account, player.getCasinoAccount());
    }

    @Test
    public void testSetAccount() {
        // given
        CasinoAccount newAccount = new CasinoAccount("NewPlayer", 100.0);
        // when
        player.setCasinoAccount(newAccount);
        // then
        assertSame(newAccount, player.getCasinoAccount());
    }

    @Test
    public void testAccountBalanceAccessible() {
        assertEquals(100.0, player.getCasinoAccount().getBalance(), 0.01);
    }

    // ── WarPlayer specific tests ─────────────────────────

    @Test
    public void testNewPlayerHasZeroBet() {
        assertEquals(0L, player.getCurrentBet());
    }

    @Test
    public void testSetCurrentBetUpdatesField() {
        // when
        player.setCurrentBet(50L);
        // then
        assertEquals(50L, player.getCurrentBet());
    }

    @Test
    public void testNewPlayerHasNoCurrentCard() {
        assertNull(player.getCurrentCard());
    }

    @Test
    public void testSetCurrentCardUpdatesField() {
        // given
        Card ace = new Card(Card.Rank.ACE, Card.Suit.SPADES);
        // when
        player.setCurrentCard(ace);
        // then
        assertSame(ace, player.getCurrentCard());
    }

    @Test
    public void testPlayReturnsNull() {
        // WarGame drives the play loop, not the player
        assertNull(player.play());
    }
}