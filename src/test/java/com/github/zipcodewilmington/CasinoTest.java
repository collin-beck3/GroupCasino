package com.github.zipcodewilmington;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.zipcodewilmington.casino.CasinoAccount;

public class CasinoTest {

    private Casino casino;
    private ConsoleUI consoleUI;
    private CasinoAccountManager casinoAccountManager;

    @BeforeEach
    public void setUp() {
        // given
        consoleUI = new ConsoleUI();
        casino    = new Casino(consoleUI);
    }

    @Test
    public void testCasinoIsNotNull() {
        assertNotNull(casino);
    }

    @Test
    public void testConsoleUIIsNotNull() {
        assertNotNull(consoleUI);
    }

    @Test
    public void testCasinoAccountManagerIsNotNull() {
        // given — CasinoAccountManager is created inside Casino constructor
        CasinoAccountManager manager = new CasinoAccountManager();
        // then
        assertNotNull(manager);
    }

    @Test
    public void testCreateAndRegisterAccount() {
        // given
        CasinoAccountManager manager = new CasinoAccountManager();
        // when
        CasinoAccount account = manager.createAccount("TestPlayer", "password");
        manager.registerAccount(account);
        // then
        assertNotNull(manager.getAccount("TestPlayer", "password"));
    }
}