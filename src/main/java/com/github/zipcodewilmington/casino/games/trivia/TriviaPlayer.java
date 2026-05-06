package com.github.zipcodewilmington.casino.games.trivia;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;

public class TriviaPlayer implements PlayerInterface {

    private CasinoAccount casinoAccount;

    public TriviaPlayer(CasinoAccount casinoAccount) {
        this.casinoAccount = casinoAccount;
    }

    @Override
    public CasinoAccount getCasinoAccount() {
        return casinoAccount;
    }

    @Override
    public void setCasinoAccount(CasinoAccount account) {
        this.casinoAccount = account;
    }

    @Override
    public <SomeReturnType> SomeReturnType play() {
        return null;
    }
}

