package com.github.zipcodewilmington.casino.games.roulette;

import java.util.Scanner;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.PlayerInterface;
import java.util.*;

/**
 * RoulettePlayer — implements PlayerInterface.
 *
 * play() is generic (SomeReturnType) per the interface contract;
 * this implementation returns String.
 *
 * Psychological dark patterns:
 * 1. NEAR-MISS DISPLAY — "💔 SO CLOSE! Stopped on 16 — you had 17!"
 * 2. HOT BOARD — scrubbed history (0/00 never shown)
 * 3. MARTINGALE TRAP — suggests doubling after every loss
 * 4. COUNTDOWN TIMER — 15s pressure on bet selection
 * 5. WIN FANFARE — huge box on wins, one quiet line on losses
 * 6. QUIT FRICTION — "You're down $X — are you sure?"
 */
public class RoulettePlayer implements PlayerInterface {

    private static final double MIN_BET = 5.0;
    private static final double BET_CAP_PCT = 0.20; // 20% of balance
    private static final int TIMER_SECS = 15;

    private final CasinoAccount casinoAccount;
    private final Scanner scanner = new Scanner(System.in);

    // ── Session tracking ─────────────────────────────────────────────
    private double startingBalance;
    private double totalWagered = 0;
    private double totalWon = 0;
    private int spinCount = 0;
    private double lastBet = MIN_BET;
    private String lastBetParam = "red";

    // Hot board — 0 and 00 are silently scrubbed (trick 2)
    private final Deque<Integer> hotBoard = new ArrayDeque<>();
    private final Map<Integer, Integer> hitCount = new HashMap<>();

    // ── Constructor ───────────────────────────────────────────────────

    public RoulettePlayer(CasinoAccount casinoAccount) {
        this.casinoAccount = casinoAccount;
    }

    // ================================================================
    // PlayerInterface
    // ================================================================

    @Override
    public CasinoAccount getCasinoAccount() {
        return casinoAccount;
    }

    /**
     * Generic play() required by PlayerInterface.
     * Returns String (session summary).
     * Called with no args — needs a game reference, so delegates to
     * play(RouletteGame) which GameInterface.run() calls directly.
     *
     * @param <SomeReturnType> inferred as String
     */
    @Override
    @SuppressWarnings("unchecked")
    public <SomeReturnType> SomeReturnType play() {
        // No game injected via this path — warn and return summary stub.
        System.out.println("[RoulettePlayer] play() called without a game." +
                " Use RouletteGame.run() to start.");
        return (SomeReturnType) "No game provided.";
    }

    // ================================================================
    // Full game session — called by RouletteGame.run()
    // ================================================================

    /**
     * Runs a full interactive roulette session.
     *
     * @param game the RouletteGame driving this session
     * @return session summary string
     */
    public String play(RouletteGame game) {
        startingBalance = casinoAccount.getBalance();
        printBanner();

        boolean keepPlaying = true;
        while (keepPlaying) {

            double balance = casinoAccount.getBalance();
            if (balance < MIN_BET) {
                System.out.println("\n💸  You're out of chips. House wins.");
                break;
            }

            printHotBoard(); // trick 2
            printBalanceLine(balance, false, 0);

            // ── Bet type with countdown (trick 4) ────────────────────
            String betType = chooseBetTypeWithTimer();
            if (betType.equals("quit")) {
                if (!confirmQuit())
                    continue; // trick 6
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
            String betParam = chooseBetParam(betType);
            if (betParam == null)
                continue;

            // ── Withdraw ──────────────────────────────────────────────
            if (!casinoAccount.withdraw(stake)) {
                System.out.println("⚠  Insufficient funds.");
                continue;
            }
            totalWagered += stake;
            lastBet = stake;
            lastBetParam = betParam;

            // ── Spin ──────────────────────────────────────────────────
            animateSpin();
            int pocket;
            if (betType.equals("straight")) {
                int chosen = betParam.equals("00")
                        ? RouletteGame.DOUBLE_ZERO
                        : Integer.parseInt(betParam);
                pocket = game.spinWeighted(chosen); // trick 1 near-miss
            } else {
                pocket = game.spin();
            }

            updateHotBoard(pocket); // trick 2 — scrubs 0/00
            spinCount++;

            System.out.printf("%n🎡  Ball landed: %-3s  [%s]%n%n",
                    game.pocketLabel(pocket), game.pocketColor(pocket).toUpperCase());

            // ── Near-miss reveal (trick 1) ────────────────────────────
            if (game.wasNearMiss()) {
                int chosen = betParam.equals("00")
                        ? RouletteGame.DOUBLE_ZERO
                        : Integer.parseInt(betParam);
                System.out.println("💔  SO CLOSE! Stopped on "
                        + game.pocketLabel(pocket)
                        + " — you had " + game.pocketLabel(chosen) + "!");
                System.out.println("    Couldn't be closer. Your number is HOT...\n");
            }

            // ── Resolve ───────────────────────────────────────────────
            double payout = game.resolve(betType, betParam, stake, pocket);
            if (payout > 0) {
                casinoAccount.deposit(payout);
                totalWon += payout;
                double profit = payout - stake;
                printWinFanfare(profit, payout); // trick 5 big celebration
                printBalanceLine(casinoAccount.getBalance(), true, profit);
            } else {
                System.out.printf("  Lost $%.2f.%n", stake); // trick 5 quiet loss
                printMartingaleTip(stake, casinoAccount.getBalance()); // trick 3
            }

            // ── Continue? ─────────────────────────────────────────────
            System.out.print("\n  Spin again? (y / n): ");
            keepPlaying = scanner.next().trim().toLowerCase().startsWith("y");
        }

        return buildSummary();
    }

    // ================================================================
    // Tricks
    // ================================================================

    // Trick 2 — Hot board: silently scrubs 0 and 00 ──────────────────

    private void updateHotBoard(int pocket) {
        if (pocket == 0 || pocket == RouletteGame.DOUBLE_ZERO)
            return; // scrubbed
        hotBoard.addLast(pocket);
        if (hotBoard.size() > 10)
            hotBoard.pollFirst();
        hitCount.merge(pocket, 1, Integer::sum);
    }

    private void printHotBoard() {
        if (hotBoard.isEmpty())
            return;
        System.out.print("\n  🔥 Last results: ");
        hotBoard.forEach(p -> System.out.print(p + " "));
        int hot = hotBoard.stream()
                .max(Comparator.comparingInt(p -> hitCount.getOrDefault(p, 0)))
                .orElse(7);
        System.out.printf("%n  🌡  Hottest this session: %d  ← bet it!%n%n", hot);
    }

    // Trick 3 — Martingale tip ────────────────────────────────────────

    private void printMartingaleTip(double lostBet, double balance) {
        double doubleBet = lostBet * 2;
        if (doubleBet <= balance * BET_CAP_PCT) {
            System.out.printf(
                    "%n  💡 Dealer tip: bet $%.2f (double up) on %s — "
                            + "one win recovers everything!%n",
                    doubleBet, lastBetParam);
        }
    }

    // Trick 4 — Countdown timer ───────────────────────────────────────

    private String chooseBetTypeWithTimer() {
        printBetMenu();
        System.out.printf("  ⏱  %d seconds to decide!  Choice: ", TIMER_SECS);

        Thread timerThread = new Thread(() -> {
            for (int s = TIMER_SECS; s > 0; s--) {
                System.out.printf("\r  ⏱  %2d seconds left!  Choice: ", s);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();

        int choice;
        try {
            choice = Integer.parseInt(scanner.next().trim());
        } catch (NumberFormatException e) {
            choice = -1;
        }

        timerThread.interrupt();
        System.out.println();

        switch (choice) {
            case 1:
                return "straight";
            case 2:
                return "split";
            case 3:
                return "street";
            case 4:
                return "corner";
            case 5:
                return "dozen";
            case 6:
                return "column";
            case 7:
                return "color";
            case 8:
                return "parity";
            case 9:
                return "half";
            default:
                return "quit";
        }
    }

    // Trick 6 — Quit friction ─────────────────────────────────────────

    private boolean confirmQuit() {
        double net = casinoAccount.getBalance() - startingBalance;
        if (net < 0) {
            System.out.printf(
                    "%n  😬  You're down $%.2f. Leave now? One spin could fix it. (y/n): ",
                    Math.abs(net));
        } else {
            System.out.print("\n  Cash out? (y/n): ");
        }
        return scanner.next().trim().toLowerCase().startsWith("y");
    }

    // Trick 5 — Asymmetric win/loss display ───────────────────────────

    private void printWinFanfare(double profit, double payout) {
        System.out.println(
                "\n  ╔══════════════════════════════════╗");
        System.out.printf(
                "  ║  🎉  WINNER!  +$%-8.2f        ║%n", profit);
        System.out.printf(
                "  ║  Payout: $%-8.2f              ║%n", payout);
        System.out.println(
                "  ╚══════════════════════════════════╝");
    }

    private void printBalanceLine(double balance, boolean showDelta, double delta) {
        if (showDelta && delta > 0)
            System.out.printf("  💰  +$%.2f  →  NEW BALANCE: $%.2f%n", delta, balance);
        else
            System.out.printf("%n  Balance: $%.2f%n", balance);
    }

    // ================================================================
    // Standard helpers
    // ================================================================

    private void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      🎡  LUCKY SPIN ROULETTE  🎡             ║");
        System.out.println("║   American Wheel · 0 · 00 · 1-36            ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    private void printBetMenu() {
        System.out.println();
        System.out.println("  ┌───┬──────────────────────────┬──────────┐");
        System.out.println("  │ 1 │ Straight  (exact number) │  35:1    │");
        System.out.println("  │ 2 │ Split     (2 numbers)    │  17:1    │");
        System.out.println("  │ 3 │ Street    (row of 3)     │  11:1    │");
        System.out.println("  │ 4 │ Corner    (block of 4)   │   8:1    │");
        System.out.println("  │ 5 │ Dozen     (12 numbers)   │   2:1    │");
        System.out.println("  │ 6 │ Column    (col 1/2/3)    │   2:1    │");
        System.out.println("  │ 7 │ Color     (red/black)    │   1:1    │");
        System.out.println("  │ 8 │ Parity    (odd/even)     │   1:1    │");
        System.out.println("  │ 9 │ High/Low  (1-18/19-36)   │   1:1    │");
        System.out.println("  │ 0 │ Walk away                           │");
        System.out.println("  └───┴──────────────────────────┴──────────┘");
    }

    private double chooseStake(double balance) {
        double maxBet = Math.floor(balance * BET_CAP_PCT * 100) / 100;
        System.out.printf("  Bet [$%.2f – $%.2f]: $", MIN_BET, maxBet);
        double stake;
        try {
            stake = Double.parseDouble(scanner.next().trim());
        } catch (NumberFormatException e) {
            return -1;
        }

        if (stake < MIN_BET) {
            System.out.printf("  ⚠  Min bet is $%.2f%n", MIN_BET);
            return chooseStake(balance);
        }
        if (stake > maxBet) {
            System.out.printf("  ⚠  Table max for you is $%.2f%n", maxBet);
            return chooseStake(balance);
        }
        return stake;
    }

    private String chooseBetParam(String betType) {
        switch (betType) {
            case "straight":
                System.out.print("  Number (0-36 or 00): ");
                break;
            case "split":
                System.out.print("  Two numbers e.g. 14,15: ");
                break;
            case "street":
                System.out.print("  First of row (1,4,7,…,34): ");
                break;
            case "corner":
                System.out.print("  Top-left of block e.g. 11: ");
                break;
            case "dozen":
                System.out.print("  Dozen (1 = 1-12, 2 = 13-24, 3): ");
                break;
            case "column":
                System.out.print("  Column (1, 2, or 3): ");
                break;
            case "color":
                System.out.print("  Color (red / black): ");
                break;
            case "parity":
                System.out.print("  Odd or Even: ");
                break;
            case "half":
                System.out.print("  Low (1-18) or High (19-36): ");
                break;
            default:
                return null;
        }
        return scanner.next().trim().toLowerCase();
    }

    private void animateSpin() {
        String[] frames = { "|", "/", "—", "\\" };
        for (int i = 0; i < 24; i++) {
            System.out.printf("\r  🎡 Spinning %s  ", frames[i % 4]);
            try {
                Thread.sleep(80);
            } catch (InterruptedException ignored) {
            }
        }
        System.out.println("\r  🎡 The ball drops...             ");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
    }

    private String buildSummary() {
        double finalBalance = casinoAccount.getBalance();
        double net = finalBalance - startingBalance;
        String summary = String.format(
                "Spins: %d | Wagered: $%.2f | Net: %s$%.2f | Balance: $%.2f",
                spinCount, totalWagered,
                net >= 0 ? "+" : "-", Math.abs(net), finalBalance);
        System.out.println("\n  " + summary);
        return summary;
    }
}