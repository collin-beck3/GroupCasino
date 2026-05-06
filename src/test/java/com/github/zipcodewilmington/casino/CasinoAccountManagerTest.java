package com.github.zipcodewilmington.casino;

/**
 * Created by leon on 7/21/2020.
 * `ArcadeAccountManager` stores, manages, and retrieves `ArcadeAccount` objects
 * it is advised that every instruction in this class is logged
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.zipcodewilmington.CasinoAccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        CasinoAccount found = manager.getAccount("TestPlayer", "password123");
        // then
        assertNotNull(found);
        assertEquals("TestPlayer", found.getPlayerName());
    }

    @Test
    public void testGetAccountFailsWithWrongPassword() {
        // given
        CasinoAccount account = manager.createAccount("TestPlayer", "password123");
        manager.registerAccount(account);
        // when
        CasinoAccount found = manager.getAccount("TestPlayer", "wrongpassword");
        // then
        assertNull(found);
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
    public void testGetCasinoAccountList() {
        // given
        manager.registerAccount(manager.createAccount("Player1", "pass1"));
        manager.registerAccount(manager.createAccount("Player2", "pass2"));
        // when
        int size = manager.getCasinoAccountList().size();
        // then
        assertEquals(2, size);
    }
}