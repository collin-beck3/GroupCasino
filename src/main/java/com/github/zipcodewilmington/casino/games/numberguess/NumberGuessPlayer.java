package com.github.zipcodewilmington.casino.games.numberguess;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;
import com.github.zipcodewilmington.utils.IOConsole;

/**
 * Created by leon on 7/21/2020.
 */
public class NumberGuessPlayer implements PlayerInterface {

    private CasinoAccount casinoAccount;

    public NumberGuessPlayer(CasinoAccount casinoAccount) {
        this.casinoAccount = casinoAccount;
    }

    @Override
    public CasinoAccount getCasinoAccount() {
        return casinoAccount;
    }

    @Override
    public Integer play() {
        IOConsole number = new IOConsole();
        while(true) {
            int numberGuess = number.getIntegerInput("Please input an integer: ");
            if(numberGuess <= 0 || numberGuess > 100) {
                System.out.println("Invalid Number.");
                continue;
            } else {
                return numberGuess;
            }
        }
    }

    @Override
    public void setCasinoAccount(CasinoAccount account) {
        
    }

}