package com.github.zipcodewilmington.casino;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CasinoAccountTest {

    private CasinoAccount account;

    @BeforeEach
    public void setUp() {
        // given — fresh account before every test
        account = new CasinoAccount("TestPlayer", 100.0);
    }

    @Test
    public void testGetPlayerName() {
        // given (setUp)
        // when
        String name = account.getPlayerName();
        // then
        assertEquals("TestPlayer", name);
    }

    @Test
    public void testGetStartingBalance() {
        // given (setUp)
        // when
        double balance = account.getBalance();
        // then
        assertEquals(100.0, balance, 0.01);
    }

    @Test
    public void testDeposit() {
        // given (setUp)
        // when
        account.deposit(50.0);
        // then
        assertEquals(150.0, account.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawSuccess() {
        // given (setUp)
        // when
        boolean result = account.withdraw(40.0);
        // then
        assertTrue(result);
        assertEquals(60.0, account.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawFailsWhenBroke() {
        // given (setUp)
        // when — try to withdraw more than balance
        boolean result = account.withdraw(200.0);
        // then
        assertFalse(result);
        assertEquals(100.0, account.getBalance(), 0.01); // balance unchanged
    }

    @Test
    public void testWithdrawExactBalance() {
        // given (setUp)
        // when — bet everything
        boolean result = account.withdraw(100.0);
        // then
        assertTrue(result);
        assertEquals(0.0, account.getBalance(), 0.01);
    }

    @Test
    public void testDepositNegativeDoesNothing() {
        // given (setUp)
        // when — bad input
        account.deposit(-50.0);
        // then — balance unchanged
        assertEquals(100.0, account.getBalance(), 0.01);
    }

    @Test
    public void testToStringContainsNameAndBalance() {
        // given (setUp)
        // when
        String result = account.toString();
        // then
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("100.00"));
    }

    @Test
    public void testAccountIsNotNull() {
        // given (setUp)
        // then
        assertNotNull(account);
    }
}
