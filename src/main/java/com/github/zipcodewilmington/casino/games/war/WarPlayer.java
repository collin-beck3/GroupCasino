package com.github.zipcodewilmington.casino.games.war;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;

public class WarPlayer implements PlayerInterface {

    // Every player needs a CasinoAccount (per the spec). It's passed
    // in when the player is created — never built here, because the
    // account has to outlive the game.
    private CasinoAccount account;

    public WarPlayer(CasinoAccount account) {
        this.account = account;
    }

    @Override
    public CasinoAccount getArcadeAccount() {
        return account;
    }

    // PlayerInterface requires a play() method, but in our setup
    // WarGame is the one that runs the game flow, not the player.
    // So this just returns null.
    //
    // The weird "<SomeReturnType>" syntax comes from the interface —
    // we have to write it that way to match what's there.
    @Override
    public <SomeReturnType> SomeReturnType play() {
        return null;
    }
}
