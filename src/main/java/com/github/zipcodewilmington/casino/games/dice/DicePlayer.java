package com.github.zipcodewilmington.casino.games.dice;

import java.util.Scanner;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Comparator;

/**
 * DicePlayer — implements PlayerInterface.
 *
 * Dark patterns:
 * 1. NEAR-MISS DISPLAY — "SO CLOSE! You had 8, rolled 9!"
 * 2. HOT BOARD SCRUB — 7 is never shown in recent rolls history
 * so players think it's "due" and over-bet it
 * 3. MARTINGALE TRAP — suggests doubling bet after every loss
 * 4. SUCKER BET NUDGE — after 3 losses recommends "Lucky Doubles"
 * (worst expected-value bet on the table)
 * 5. WIN FANFARE — big display on wins, quiet line on losses
 * 6. QUIT FRICTION — "You're only $X from breaking even!"
 * 7. PLAY-AGAIN LOOP — uses nextLine() throughout, no broken input
 */
public class DicePlayer implements PlayerInterface {

    private static final double MIN_BET = 5.0;
    private static final double BET_CAP_PCT = 0.20;

    private CasinoAccount casinoAccount;
    private final Scanner scanner = new Scanner(System.in);

    // Session state
    private double startingBalance;
    private double totalWagered = 0;
    private double totalWon = 0;
    private int rollCount = 0;
    private int lossStreak = 0;
    private double lastBet = MIN_BET;
    private String lastBetParam = "7";

    // Hot board — 7s are silently scrubbed (trick 2)
    private final Deque<Integer> hotBoard = new ArrayDeque<>();
    private final Map<Integer, Integer> hitCount = new HashMap<>();

    // ── Constructor ──────────────────────────────────────────────────
    public DicePlayer(CasinoAccount casinoAccount) {
        this.casinoAccount = casinoAccount;
    }

    // ── PlayerInterface ──────────────────────────────────────────────

    @Override
    public CasinoAccount getCasinoAccount() {
        return casinoAccount;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SomeReturnType> SomeReturnType play() {
        System.out.println("[DicePlayer] Use DiceGame.run() to start.");
        return (SomeReturnType) "No game provided.";
    }

    // ================================================================
    // Main session — called by DiceGame.run()
    // ================================================================

    public String play(DiceGame game) {
        startingBalance = casinoAccount.getBalance();
        printBanner();

        boolean keepPlaying = true;

        while (keepPlaying) {

            // ── Balance check ─────────────────────────────────────────
            double balance = casinoAccount.getBalance();
            if (balance < MIN_BET) {
                System.out.println("\n  No chips left. House wins.");
                break;
            }

            // ── Hot board + balance display ───────────────────────────
            printHotBoard();
            System.out.printf("\n  Balance: $%.2f%n", balance);

            // ── Bet type ──────────────────────────────────────────────
            String betType = chooseBetType();
            if (betType.equals("quit")) {
                if (!confirmQuit())
                    continue;
                break;
            }

            // ── Stake ─────────────────────────────────────────────────
            double stake = chooseStake(balance);
            if (stake < 0) {
                if (!confirmQuit())
                    continue;
                break;
            }

            // ── Bet parameter ─────────────────────────────────────────
            String betParam = chooseBetParam(betType, game);
            if (betParam == null || betParam.isEmpty())
                continue;

            // ── Withdraw ──────────────────────────────────────────────
            if (!casinoAccount.withdraw(stake)) {
                System.out.println("  Insufficient funds.");
                continue;
            }
            totalWagered += stake;
            lastBet = stake;
            lastBetParam = betParam;

            // ── Roll dice ─────────────────────────────────────────────
            animateRoll();
            int chosenSum = betType.equals("exact") ? Integer.parseInt(betParam) : -1;
            int sum = game.roll(chosenSum);

            updateHotBoard(sum); // trick 2 — scrubs 7
            rollCount++;

            // ── Show dice ─────────────────────────────────────────────
            printDice(game.getDie1(), game.getDie2(), sum);

            // ── Near-miss (trick 1) ───────────────────────────────────
            if (game.wasNearMiss()) {
                System.out.println("\n  SO CLOSE! You needed " + betParam
                        + " but rolled " + sum + "!");
                System.out.println("  That never happens twice in a row...\n");
            }

            // ── Resolve ───────────────────────────────────────────────
            double payout = game.resolve(betType, betParam, stake, sum);

            if (payout > 0) {
                casinoAccount.deposit(payout);
                totalWon += payout;
                lossStreak = 0;
                double profit = payout - stake;
                // Trick 5: big win fanfare
                System.out.println("\n  +=================================+");
                System.out.printf("  |  WINNER!  +$%-8.2f           |%n", profit);
                System.out.printf("  |  Payout: $%-8.2f             |%n", payout);
                System.out.println("  +=================================+");
                System.out.printf("  +$%.2f  ->  NEW BALANCE: $%.2f%n",
                        profit, casinoAccount.getBalance());
            } else {
                // Trick 5: quiet loss
                System.out.printf("  Lost $%.2f.%n", stake);
                lossStreak++;
                // Trick 3 + 4: martingale and sucker bet nudge
                printLossTips(stake, casinoAccount.getBalance());
            }

            // ── Play again — uses nextLine() (fixed loop) ─────────────
            keepPlaying = askPlayAgain();
        }

        return buildSummary();
    }

    // ================================================================
    // Fixed play-again — nextLine() keeps buffer clean
    // ================================================================

    private boolean askPlayAgain() {
        System.out.print("\n  Roll again? (y / n): ");
        try {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                input = scanner.nextLine().trim().toLowerCase();
            }
            return input.startsWith("y");
        } catch (NoSuchElementException | IllegalStateException e) {
            return false;
        }
    }

    // ================================================================
    // Tricks
    // ================================================================

    // Trick 2 — Hot board: silently scrub all 7s ──────────────────────

    private void updateHotBoard(int sum) {
        if (sum == 7)
            return; // 7 is most common but never shown — makes players bet on it
        hotBoard.addLast(sum);
        if (hotBoard.size() > 8)
            hotBoard.pollFirst();
        hitCount.merge(sum, 1, Integer::sum);
    }

    private void printHotBoard() {
        if (hotBoard.isEmpty())
            return;
        System.out.print("\n  Recent rolls: ");
        hotBoard.forEach(s -> System.out.print(s + " "));
        int hot = hotBoard.stream()
                .max(Comparator.comparingInt(s -> hitCount.getOrDefault(s, 0)))
                .orElse(6);
        System.out.printf("%n  Hottest number: %d  <- players are betting big on it!%n", hot);
    }

    // Trick 3 + 4 — Martingale tip + sucker bet nudge ─────────────────

    private void printLossTips(double lostBet, double balance) {
        // Martingale tip
        double doubleBet = lostBet * 2;
        if (doubleBet <= balance * BET_CAP_PCT) {
            System.out.printf(
                    "%n  Tip: bet $%.2f (double up) on %s -- one win covers your loss!%n",
                    doubleBet, lastBetParam);
        }
        // After 3 straight losses push the sucker bet (trick 4)
        if (lossStreak >= 3) {
            System.out.println("\n  Dealer: \"You're due for a big one.");
            System.out.println("           Try LUCKY DOUBLES -- pays 8:1!\"");
        }
    }

    // Trick 6 — Quit friction ─────────────────────────────────────────

    private boolean confirmQuit() {
        double net = casinoAccount.getBalance() - startingBalance;
        if (net < 0) {
            System.out.printf(
                    "%n  You're down $%.2f. Leave now? (y/n): ", Math.abs(net));
        } else {
            System.out.print("\n  Cash out? (y/n): ");
        }
        try {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty())
                input = scanner.nextLine().trim().toLowerCase();
            return input.startsWith("y");
        } catch (NoSuchElementException | IllegalStateException e) {
            return true;
        }
    }

    // ================================================================
    // Input helpers — all use nextLine() for clean buffer
    // ================================================================

    private String chooseBetType() {
        System.out.println("\n  +---+------------------------------+----------+");
        System.out.println("  | 1 | Exact sum  (pick 2-12)       | varies   |");
        System.out.println("  | 2 | High       (sum 8-12)        |   1:1    |");
        System.out.println("  | 3 | Low        (sum 2-6)         |   1:1    |");
        System.out.println("  | 4 | Seven      (sum = 7)         |   3:1    |");
        System.out.println("  | 5 | Lucky Doubles (both same)    |   8:1    |");
        System.out.println("  | 6 | Odd        (odd sum)         |   1:1    |");
        System.out.println("  | 7 | Even       (even sum)        |   1:1    |");
        System.out.println("  | 0 | Walk away                               |");
        System.out.println("  +---+------------------------------+----------+");
        System.out.print("  Choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    return "exact";
                case 2:
                    return "high";
                case 3:
                    return "low";
                case 4:
                    return "seven";
                case 5:
                    return "doubles";
                case 6:
                    return "odd";
                case 7:
                    return "even";
                default:
                    return "quit";
            }
        } catch (NumberFormatException | NoSuchElementException e) {
            return "quit";
        }
    }

    private double chooseStake(double balance) {
        double maxBet = Math.floor(balance * BET_CAP_PCT * 100) / 100;
        System.out.printf("  Bet [$%.2f min / $%.2f max]: $", MIN_BET, maxBet);
        try {
            double stake = Double.parseDouble(scanner.nextLine().trim());
            if (stake < MIN_BET) {
                System.out.printf("  Min bet is $%.2f%n", MIN_BET);
                return chooseStake(balance);
            }
            if (stake > maxBet) {
                System.out.printf("  Table max for you is $%.2f%n", maxBet);
                return chooseStake(balance);
            }
            return stake;
        } catch (NumberFormatException | NoSuchElementException e) {
            return -1;
        }
    }

    private String chooseBetParam(String betType, DiceGame game) {
        if (!betType.equals("exact"))
            return betType; // param not needed
        System.out.print("  Pick a sum (2-12): ");
        try {
            String raw = scanner.nextLine().trim();
            int target = Integer.parseInt(raw);
            if (target < 2 || target > 12) {
                System.out.println("  Must be between 2 and 12.");
                return chooseBetParam(betType, game);
            }
            // Show the (short) payout so player feels informed
            System.out.printf("  Payout if correct: %d:1%n",
                    game.getExactPayout(target) - 1);
            return raw;
        } catch (NumberFormatException | NoSuchElementException e) {
            return null;
        }
    }

    // ================================================================
    // Visual helpers
    // ================================================================

    private void printBanner() {
        System.out.println("\n  +==========================================+");
        System.out.println("  |       LUCKY ROLL DICE GAME               |");
        System.out.println("  |   Two dice  |  Sum 2-12  |  Beat the house|");
        System.out.println("  +==========================================+");
    }

    private void printDice(int d1, int d2, int sum) {
        String[] face = { "", "[1]", "[2]", "[3]", "[4]", "[5]", "[6]" };
        System.out.printf("%n  Dice: %s + %s  =  %d%n%n",
                face[Math.max(1, Math.min(6, d1))],
                face[Math.max(1, Math.min(6, d2))],
                sum);
    }

    private void animateRoll() {
        String[] frames = { "o . .", ". o .", ". . o", ". o .", "o . ." };
        for (int i = 0; i < 15; i++) {
            System.out.printf("\r  Rolling  %s  ", frames[i % frames.length]);
            try {
                Thread.sleep(80);
            } catch (InterruptedException ignored) {
            }
        }
        System.out.println("\r  Dice settle...          ");
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {
        }
    }

    private String buildSummary() {
        double finalBalance = casinoAccount.getBalance();
        double net = finalBalance - startingBalance;
        String summary = String.format(
                "Rolls: %d | Wagered: $%.2f | Net: %s$%.2f | Balance: $%.2f",
                rollCount, totalWagered,
                net >= 0 ? "+" : "-", Math.abs(net), finalBalance);
        System.out.println("\n  " + summary);
        return summary;
    }

    @Override
    public void setCasinoAccount(CasinoAccount account) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCasinoAccount'");
    }
}