package com.github.zipcodewilmington.casino.games.war;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.casino.shared.Card;


public class WarPlayer implements PlayerInterface {

    // Every player needs a CasinoAccount (per the spec). It's passed
    // in when the player is created — never built here, because the
    // account has to outlive the game.
    private CasinoAccount account;
    private long currentBet;
    private Card currentCard;

    public WarPlayer(CasinoAccount account) {
        this.account = account;
        this.currentBet = 0L;
        this.currentCard = null;
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

    public long getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(long bet) {
        this.currentBet = bet;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card card) {
        this.currentCard = card;
    }
}
