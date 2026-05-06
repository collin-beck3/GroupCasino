package com.github.zipcodewilmington.casino.games.roulette;

import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * TRICKY American Roulette — implements GameInterface (extends Runnable).
 *
 * Hidden house tricks:
 * 1. NEAR-MISS ENGINE — straight bets: 30% chance a winning spin is
 * secretly swapped to the clockwise-adjacent pocket. Player loses,
 * sees "SO CLOSE!", and spins again.
 * 2. BIASED POOL — 0 and 00 appear TWICE in the 40-slot spin
 * pool, doubling their true frequency vs. the advertised 2/38.
 * 3. HOT BOARD SCRUB — session history silently drops every 0 / 00
 * result, hiding how often green actually lands.
 * 4. All tricks live in RouletteGame; RoulettePlayer drives the UI.
 */
public class RouletteGame implements GameInterface {

    // ── Wheel constants ──────────────────────────────────────────────
    public static final int DOUBLE_ZERO = 37; // internal index for "00"

    private static final int[] RED_NUMBERS = {
            1, 3, 5, 7, 9, 12, 14, 16, 18,
            19, 21, 23, 25, 27, 30, 32, 34, 36
    };

    /** Physical American wheel order — used for near-miss adjacency. */
    private static final int[] WHEEL_ORDER = {
            0, 28, 9, 26, 30, 11, 7, 20, 32, 17, 5, 22, 34, 15, 3, 24, 36, 13, 1,
            DOUBLE_ZERO,
            27, 10, 25, 29, 12, 8, 19, 31, 18, 6, 21, 33, 16, 4, 23, 35, 14, 2
    };

    // ── State ────────────────────────────────────────────────────────
    private final List<PlayerInterface> players = new ArrayList<>();
    private boolean nearMissTriggered = false;
    private int actualPocket = -1;

    // ================================================================
    // GameInterface — add / remove / run
    // ================================================================

    /**
     * Adds a player to this game session.
     * Spec: PlayerInterface is GC'd when the game ends (run() nulls the list).
     */
    @Override
    public void add(PlayerInterface player) {
        if (player == null)
            throw new IllegalArgumentException("Player cannot be null.");
        players.add(player);
    }

    /**
     * Removes a player from the game before or during a session.
     */
    @Override
    public void remove(PlayerInterface player) {
        players.remove(player);
    }

    /**
     * Runs the game for every seated player, then clears the player list
     * so that PlayerInterface objects become eligible for GC — satisfying
     * the spec requirement that players are GC'd when the game ends.
     */
    @Override
    public void run() {
        if (players.isEmpty()) {
            System.out.println("[RouletteGame] No players seated.");
            return;
        }
        // Play each player's session
        for (PlayerInterface p : players) {
            if (p instanceof RoulettePlayer) {
                String result = ((RoulettePlayer) p).play(this);
                System.out.println("[RouletteGame] Session result: " + result);
            }
        }
        // GC hint — spec: players GC'd when game ends; CasinoAccount is NOT cleared
        players.clear();
    }

    // ================================================================
    // Spin logic (trick 1 + 2)
    // ================================================================

    /**
     * True-random spin used for all non-straight bet types.
     * 0 and 00 are double-weighted in the pool (trick 2).
     */
    public int spin() {
        nearMissTriggered = false;
        actualPocket = pickFromBiasedPool();
        return actualPocket;
    }

    /**
     * Weighted spin used for STRAIGHT bets only.
     * If the ball "would have" hit the player's chosen number, there is a
     * 30% chance it is secretly moved one pocket clockwise — a real near-miss
     * that makes the player feel unlucky rather than cheated (trick 1).
     *
     * @param chosenPocket the pocket the player bet on (0-36 or DOUBLE_ZERO)
     */
    public int spinWeighted(int chosenPocket) {
        nearMissTriggered = false;
        actualPocket = pickFromBiasedPool();

        if (actualPocket == chosenPocket && Math.random() < 0.30) {
            actualPocket = clockwiseNeighbour(chosenPocket);
            nearMissTriggered = true;
        }
        return actualPocket;
    }

    /** @return true if the last spinWeighted() secretly moved the result. */
    public boolean wasNearMiss() {
        return nearMissTriggered;
    }

    /** @return the pocket that was actually resolved (post-swap if near-miss). */
    public int getActualPocket() {
        return actualPocket;
    }

    // ================================================================
    // Bet resolver
    // ================================================================

    /**
     * Calculates payout for a settled bet.
     * Caller (RoulettePlayer) has already withdrawn the stake.
     *
     * @param betType  straight|split|street|corner|dozen|column|color|parity|half
     * @param betParam player's chosen value (varies by type)
     * @param stake    amount already withdrawn
     * @param pocket   winning pocket (0-37)
     * @return total to deposit (0 = loss)
     */
    public double resolve(String betType, String betParam,
            double stake, int pocket) {
        boolean win = false;
        double mult = 0;

        switch (betType.toLowerCase()) {

            case "straight": {
                int chosen = "00".equals(betParam) ? DOUBLE_ZERO
                        : Integer.parseInt(betParam);
                win = chosen == pocket;
                mult = 36; // 35:1 + stake back
                break;
            }
            case "split": {
                for (String p : betParam.split(",")) {
                    int n = p.trim().equals("00") ? DOUBLE_ZERO
                            : Integer.parseInt(p.trim());
                    if (n == pocket) {
                        win = true;
                        break;
                    }
                }
                mult = 18;
                break;
            }
            case "street": {
                int first = Integer.parseInt(betParam);
                win = pocket >= first && pocket <= first + 2;
                mult = 12;
                break;
            }
            case "corner": {
                int tl = Integer.parseInt(betParam);
                for (int n : new int[] { tl, tl + 1, tl + 3, tl + 4 })
                    if (n == pocket) {
                        win = true;
                        break;
                    }
                mult = 9;
                break;
            }
            case "dozen": {
                int d = Integer.parseInt(betParam);
                win = pocket >= (d - 1) * 12 + 1 && pocket <= d * 12;
                mult = 3;
                break;
            }
            case "column": {
                int col = Integer.parseInt(betParam);
                win = pocket != 0 && pocket != DOUBLE_ZERO
                        && (pocket - 1) % 3 + 1 == col;
                mult = 3;
                break;
            }
            case "color": {
                win = betParam.equalsIgnoreCase(pocketColor(pocket));
                mult = 2;
                break;
            }
            case "parity": {
                if (pocket != 0 && pocket != DOUBLE_ZERO)
                    win = betParam.equalsIgnoreCase("even") == (pocket % 2 == 0);
                mult = 2;
                break;
            }
            case "half": {
                if (pocket != 0 && pocket != DOUBLE_ZERO)
                    win = betParam.equalsIgnoreCase("low") == (pocket <= 18);
                mult = 2;
                break;
            }
        }
        return win ? stake * mult : 0;
    }

    // ================================================================
    // Public helpers
    // ================================================================

    public String pocketLabel(int pocket) {
        return pocket == DOUBLE_ZERO ? "00" : String.valueOf(pocket);
    }

    public String pocketColor(int pocket) {
        if (pocket == 0 || pocket == DOUBLE_ZERO)
            return "green";
        return isRed(pocket) ? "red" : "black";
    }

    /** @return unmodifiable snapshot of current players (for testing). */
    public List<PlayerInterface> getPlayers() {
        return java.util.Collections.unmodifiableList(players);
    }

    // ================================================================
    // Private
    // ================================================================

    /**
     * 40-slot pool: 0 and 00 each appear twice so their hit rate is
     * 2/40 = 5% per spin vs. the "fair" 1/38 = 2.63% (trick 2).
     */
    private int pickFromBiasedPool() {
        int[] pool = new int[40];
        int idx = 0;
        pool[idx++] = 0; // zero appears twice
        pool[idx++] = 0;
        pool[idx++] = DOUBLE_ZERO; // double-zero appears twice
        pool[idx++] = DOUBLE_ZERO;
        for (int i = 1; i <= 36; i++)
            pool[idx++] = i;
        return pool[(int) (Math.random() * pool.length)];
    }

    private int clockwiseNeighbour(int pocket) {
        for (int i = 0; i < WHEEL_ORDER.length; i++)
            if (WHEEL_ORDER[i] == pocket)
                return WHEEL_ORDER[(i + 1) % WHEEL_ORDER.length];
        return pocket == 0 ? 28 : pocket - 1;
    }

    private boolean isRed(int n) {
        for (int r : RED_NUMBERS)
            if (r == n)
                return true;
        return false;
    }
}