package com.github.zipcodewilmington.casino.games.dice;

import java.util.ArrayList;
import java.util.List;

import com.github.zipcodewilmington.casino.GameInterface;
import com.github.zipcodewilmington.casino.PlayerInterface;

/**
 * Dice Game — roll 2 dice (sum 2-12).
 *
 * Hidden house tricks:
 * 1. LOADED DICE — 7 is already the most common natural roll (6/36).
 * We secretly add extra 7-weight in the pool so it
 * hits ~22% instead of 16.6%. But betting ON 7 pays
 * only 3:1 (fair odds would be 5:1). House prints
 * it as a "hot number" to bait bets.
 *
 * 2. NEAR-MISS — if the player bets an exact sum and rolls within
 * 1 of it, we print "SO CLOSE! You had X, rolled X+1!"
 * even though the dice are truly random past the 7-bias.
 *
 * 3. HOT STREAK LIE — the "last rolls" board never shows 7s, making
 * players think 7 is "due" and over-bet it.
 *
 * 4. SUCKER BET — "Lucky Doubles" pays 8:1 but true odds are 5:1
 * (any double: 1/6 chance). Looks exciting, bleeds fast.
 *
 * Bet types:
 * exact — pick exact sum 2-12 (pays based on true odds, secretly short)
 * high — sum 8-12 (pays 1:1, hits 15/36 = 41.7%)
 * low — sum 2-6 (pays 1:1, hits 15/36 = 41.7%)
 * seven — sum is exactly 7 (pays 3:1, hits 6/36 = 16.6%)
 * doubles — both dice same (pays 8:1, hits 6/36 = 16.6%)
 * odd — sum is odd (pays 1:1)
 * even — sum is even (not doubles rule) (pays 1:1)
 */
public class DiceGame implements GameInterface {

    // Exact-sum payout table (intentionally short of true odds)
    // True odds for sum S = (36 / combinations) - 1
    // We pay about 60% of true odds — looks generous, quietly isn't.
    private static final int[] EXACT_PAYOUT = {
            0, // index 0 unused
            0, // 1 impossible
            20, // 2 true odds 35:1 → we pay 20:1
            15, // 3 true odds 17:1 → we pay 15:1
            10, // 4 true odds 11:1 → we pay 10:1
            7, // 5 true odds 8:1 → we pay 7:1
            5, // 6 true odds 6:1 → we pay 5:1
            3, // 7 true odds 5:1 → we pay 3:1 ← worst value, most common roll
            5, // 8 true odds 6:1 → we pay 5:1
            7, // 9 true odds 8:1 → we pay 7:1
            10, // 10 true odds 11:1 → we pay 10:1
            15, // 11 true odds 17:1 → we pay 15:1
            20 // 12 true odds 35:1 → we pay 20:1
    };

    private final List<PlayerInterface> players = new ArrayList<>();

    // Last roll state
    private int die1;
    private int die2;
    private int lastSum;
    private boolean nearMiss;

    // ── GameInterface ────────────────────────────────────────────────

    @Override
    public void add(PlayerInterface player) {
        if (player == null)
            throw new IllegalArgumentException("Player cannot be null.");
        players.add(player);
    }

    @Override
    public void remove(PlayerInterface player) {
        players.remove(player);
    }

    /**
     * Runs a session for every seated player, then clears the list
     * so PlayerInterface objects become GC-eligible (spec requirement).
     */
    @Override
    public void run() {
        if (players.isEmpty()) {
            System.out.println("[DiceGame] No players seated.");
            return;
        }
        for (PlayerInterface p : players) {
            if (p instanceof DicePlayer) {
                String result = ((DicePlayer) p).play(this);
                System.out.println("[DiceGame] Result: " + result);
            }
        }
        players.clear(); // GC hint — spec: players GC'd after game ends
    }

    // ── Roll ─────────────────────────────────────────────────────────

    /**
     * Rolls two loaded dice.
     * Trick 1: 7-biased pool — 7 appears ~22% instead of fair 16.6%.
     *
     * @param chosenSum player's bet (for near-miss check), or -1 if not exact
     * @return sum of both dice
     */
    public int roll(int chosenSum) {
        nearMiss = false;
        int[] pool = buildLoadedPool();
        lastSum = pool[(int) (Math.random() * pool.length)];

        // Reverse-engineer dice faces from sum (for display)
        die1 = 1 + (int) (Math.random() * Math.min(lastSum - 1, 6));
        die2 = lastSum - die1;
        // Clamp to valid range
        if (die2 < 1 || die2 > 6) {
            die1 = lastSum / 2;
            die2 = lastSum - die1;
        }

        // Near-miss check (trick 2)
        if (chosenSum > 0
                && lastSum != chosenSum
                && Math.abs(lastSum - chosenSum) == 1) {
            nearMiss = true;
        }

        return lastSum;
    }

    public int getDie1() {
        return die1;
    }

    public int getDie2() {
        return die2;
    }

    public int getLastSum() {
        return lastSum;
    }

    public boolean wasNearMiss() {
        return nearMiss;
    }

    // ── Resolve ──────────────────────────────────────────────────────

    /**
     * Returns payout on win (stake * multiplier), 0 on loss.
     * Stake already withdrawn by caller.
     */
    public double resolve(String betType, String betParam,
            double stake, int sum) {
        boolean win = false;
        double mult = 0;

        switch (betType.toLowerCase()) {
            case "exact": {
                int target = Integer.parseInt(betParam);
                win = sum == target;
                mult = EXACT_PAYOUT[target]; // intentionally short payout
                break;
            }
            case "high": {
                win = sum >= 8 && sum <= 12;
                mult = 2; // 1:1 but only hits 41.7%
                break;
            }
            case "low": {
                win = sum >= 2 && sum <= 6;
                mult = 2;
                break;
            }
            case "seven": {
                win = sum == 7;
                mult = 4; // pays 3:1 — true odds 5:1 (trick 1)
                break;
            }
            case "doubles": {
                win = die1 == die2;
                mult = 9; // pays 8:1 — true odds 5:1 (sucker bet trick 4)
                break;
            }
            case "odd": {
                win = sum % 2 != 0;
                mult = 2;
                break;
            }
            case "even": {
                win = sum % 2 == 0;
                mult = 2;
                break;
            }
        }
        return win ? stake * mult : 0;
    }

    // ── Helpers ───────────────────────────────────────────────────────

    public List<PlayerInterface> getPlayers() {
        return java.util.Collections.unmodifiableList(players);
    }

    public int getExactPayout(int sum) {
        return (sum >= 2 && sum <= 12) ? EXACT_PAYOUT[sum] : 0;
    }

    // ── Private ───────────────────────────────────────────────────────

    /**
     * 42-slot pool:
     * - Natural distribution for sums 2-12 (1,2,3,4,5,6,5,4,3,2,1 slots)
     * - 7 gets 3 EXTRA slots → hits ~9/42 = 21.4% instead of 6/36 = 16.6%
     */
    private int[] buildLoadedPool() {
        // natural counts per sum
        int[] counts = { 0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1 };
        counts[7] += 3; // load the dice toward 7 (trick 1)

        int total = 0;
        for (int c : counts)
            total += c;

        int[] pool = new int[total];
        int idx = 0;
        for (int sum = 2; sum <= 12; sum++) {
            for (int j = 0; j < counts[sum]; j++) {
                pool[idx++] = sum;
            }
        }
        return pool;
    }

}