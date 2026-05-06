package com.github.zipcodewilmington;

/**
 * Created by leon on 7/21/2020.
 * `ArcadeAccountManager` stores, manages, and retrieves `ArcadeAccount` objects
 * it is advised that every instruction in this class is logged
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.zipcodewilmington.casino.CasinoAccount;

public class CasinoAccountManagerTest {

    private CasinoAccountManager manager;

    @BeforeEach
    public void setUp() {
        // given
        manager = new CasinoAccountManager();
    }

    @Test
    public void testManagerIsNotNull() {
        assertNotNull(manager);
    }

    @Test
    public void testCreateAccount() {
        // when
        CasinoAccount account = manager.createAccount("TestPlayer", "password123");
        // then
        assertNotNull(account);
        assertEquals("TestPlayer", account.getPlayerName());
    }

    @Test
    public void testCreateAccountHasStartingBalance() {
        // when
        CasinoAccount account = manager.createAccount("TestPlayer", "password123");
        // then — starting balance should be 100.0
        assertEquals(100.0, account.getBalance(), 0.01);
    }

    @Test
    public void testRegisterAccount() {
        // given
        CasinoAccount account = manager.createAccount("TestPlayer", "password123");
        // when
        manager.registerAccount(account);
        // then
        assertEquals(1, manager.getCasinoAccountList().size());
    }

    @Test
    public void testGetAccountSuccess() {
        // given
        CasinoAccount account = manager.createAccount("TestPlayer", "password123");
        manager.registerAccount(account);
        // when
        CasinoAccount found = manager.getAccount("TestPlayer", "anypassword");
        // then
        assertNotNull(found);
        assertEquals("TestPlayer", found.getPlayerName());
    }

    @Test
    public void testGetAccountFailsWithWrongName() {
        // given
        CasinoAccount account = manager.createAccount("TestPlayer", "password123");
        manager.registerAccount(account);
        // when
        CasinoAccount found = manager.getAccount("WrongName", "password123");
        // then
        assertNull(found);
    }

    @Test
    public void testGetAccountReturnsNullWhenListEmpty() {
        // when — no accounts registered yet
        CasinoAccount found = manager.getAccount("TestPlayer", "password123");
        // then
        assertNull(found);
    }

    @Test
    public void testRegisterMultipleAccounts() {
        // given
        manager.registerAccount(manager.createAccount("Player1", "pass1"));
        manager.registerAccount(manager.createAccount("Player2", "pass2"));
        // when
        int size = manager.getCasinoAccountList().size();
        // then
        assertEquals(2, size);
    }
}