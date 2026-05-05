package com.github.zipcodewilmington.casino.games.roulette;

import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;

import java.util.ArrayList;
import java.util.List;

public class RouletteGame implements GameInterface {

    private List<PlayerInterface> players = new ArrayList<>();

    @Override
    public void add(PlayerInterface player) {
        players.add(player);
    }

    @Override
    public void remove(PlayerInterface player) {
        players.remove(player);
    }

    @Override
    public void run() {
        System.out.println("Starting Roulette...");

        for (PlayerInterface player : players) {
            System.out.println((String) player.play());
            System.out.println(player.getCasinoAccount());
        }

        players.clear();
    }
}