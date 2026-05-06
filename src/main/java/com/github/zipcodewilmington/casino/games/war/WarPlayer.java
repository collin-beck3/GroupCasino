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
    public CasinoAccount getCasinoAccount() {
        return account;
    }

    @Override
    public void setCasinoAccount(CasinoAccount casinoAccount) {
        this.account = casinoAccount;
    }

    @Override
    public <SomeReturnType> SomeReturnType play() {
        return null;
    }
}
