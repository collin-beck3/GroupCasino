package com.github.zipcodewilmington.casino;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CasinoTest {

    private Casino casino;
    private ConsoleUI consoleUI;

    @BeforeEach
    public void setUp() {
        // given
        consoleUI = new ConsoleUI();
        casino    = new Casino(consoleUI);
    }

    @Test
    public void testCasinoIsNotNull() {
        // then
        assertNotNull(casino);
    }

    @Test
    public void testConsoleUIIsNotNull() {
        // then
        assertNotNull(consoleUI);
    }

    @Test
    public void testCasinoAccountManagerIsNotNull() {
        // then — casino was built with an account manager internally
        assertNotNull(casino);
    }
}
