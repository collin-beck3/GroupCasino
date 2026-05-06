package com.github.zipcodewilmington.casino;

/**
 * Created by leon on 7/21/2020.
 * All players of a game should abide by `PlayerInterface`.
 * All players must have reference to the `ArcadeAccount` used to log into the `Arcade` system.
 * All players are capable of `play`ing a game.
 */

public interface PlayerInterface {

    /**
     * @return the CasinoAccount used to log into the Casino system
     */
    CasinoAccount getCasinoAccount();

    /**
     * @param account the CasinoAccount to assign to this player
     */
    void setCasinoAccount(CasinoAccount account);

    /**
     * Defines how a specific implementation of PlayerInterface plays their game.
     * @param <SomeReturnType> specify any return type you would like
     * @return whatever return value you would like
     */
    <SomeReturnType> SomeReturnType play();
}
