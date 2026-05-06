package com.github.zipcodewilmington.casino.games.roulette;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.github.zipcodewilmington.casino.CasinoAccount;

public class RoulettePlayerTest {

    private CasinoAccount account;
    private RoulettePlayer player;

    @BeforeEach
    public void setUp() {
        // given
        account = new CasinoAccount("TestPlayer", 300.0);
        player  = new RoulettePlayer(account);
    }

    @Test
    public void testPlayerIsNotNull() {
        assertNotNull(player);
    }

    @Test
    public void testGetAccountReturnsSameObject() {
        assertSame(account, player.getCasinoAccount());
    }

    @Test
    public void testSetAccount() {
        // given
        CasinoAccount newAccount = new CasinoAccount("NewPlayer", 50.0);
        // when
        player.setCasinoAccount(newAccount);
        // then
        assertSame(newAccount, player.getCasinoAccount());
    }

    @Test
    public void testAccountBalanceAccessible() {
        assertEquals(300.0, player.getCasinoAccount().getBalance(), 0.01);
    }
}
