package com.github.zipcodewilmington.casino;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CasinoAccountTest {

    private CasinoAccount account;

    @Before
    public void setUp() {
        account = new CasinoAccount("TestPlayer", 100.0);
    }

    @Test
    public void testGetPlayerName() {
        // given (setUp)
        // when
        String name = account.getPlayerName();
        // then
        Assert.assertEquals("TestPlayer", name);
    }
}
