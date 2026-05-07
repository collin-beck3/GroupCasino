package com.github.zipcodewilmington.casino.games.roulette;

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
    private static final double BET_CAP_PCT = 0.20;
    private static final int TIMER_SECS = 15;

    private CasinoAccount casinoAccount;
    private final Scanner scanner = new Scanner(System.in);

    private double startingBalance;
    private double totalWagered = 0;
    private double totalWon = 0;
    private int spinCount = 0;
    private double lastBet = MIN_BET;
    private String lastBetParam = "red";

    private final Deque<Integer> hotBoard = new ArrayDeque<>();
    private final Map<Integer, Integer> hitCount = new HashMap<>();

    public RoulettePlayer(CasinoAccount casinoAccount) {
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
    @SuppressWarnings("unchecked")
    public <SomeReturnType> SomeReturnType play() {
        System.out.println("[RoulettePlayer] play() called without a game. Use RouletteGame.run() to start.");
        return (SomeReturnType) "No game provided.";
    }

    public String play(RouletteGame game) {
        startingBalance = casinoAccount.getBalance();
        printBanner();

        boolean keepPlaying = true;

        while (keepPlaying) {
            double balance = casinoAccount.getBalance();

            if (balance < MIN_BET) {
                System.out.println("\nYou're out of chips. House wins.");
                break;
            }

            printHotBoard();
            printBalanceLine(balance, false, 0);

            String betType = chooseBetTypeWithTimer();

            if (betType.equals("quit")) {
                if (!confirmQuit()) {
                    continue;
                }
                break;
            }

            double stake = chooseStake(balance);

            if (stake < 0) {
                if (!confirmQuit()) {
                    continue;
                }
                break;
            }

            String betParam = chooseBetParam(betType);

            if (betParam == null) {
                continue;
            }

            if (!casinoAccount.withdraw(stake)) {
                System.out.println("Insufficient funds.");
                continue;
            }

            totalWagered += stake;
            lastBet = stake;
            lastBetParam = betParam;

            animateSpin();

            int pocket;

            if (betType.equals("straight")) {
                int chosen = betParam.equals("00")
                        ? RouletteGame.DOUBLE_ZERO
                        : Integer.parseInt(betParam);

                pocket = game.spinWeighted(chosen);
            } else {
                pocket = game.spin();
            }

            updateHotBoard(pocket);
            spinCount++;

            System.out.printf("%nBall landed: %-3s [%s]%n%n",
                    game.pocketLabel(pocket),
                    game.pocketColor(pocket).toUpperCase());

            if (game.wasNearMiss()) {
                int chosen = betParam.equals("00")
                        ? RouletteGame.DOUBLE_ZERO
                        : Integer.parseInt(betParam);

                System.out.println("SO CLOSE! Stopped on "
                        + game.pocketLabel(pocket)
                        + " — you had "
                        + game.pocketLabel(chosen)
                        + "!");
                System.out.println("  One pocket away. Your number is due...\n");
            }

            double payout = game.resolve(betType, betParam, stake, pocket);

            if (payout > 0) {
                casinoAccount.deposit(payout);
                totalWon += payout;

                double profit = payout - stake;

                printWinFanfare(profit, payout);
                printBalanceLine(casinoAccount.getBalance(), true, profit);
            } else {
                System.out.printf("Lost $%.2f.%n", stake);
                printMartingaleTip(stake, casinoAccount.getBalance());
            }

            // ── FIXED: play again uses askPlayAgain() ─────────────────
            keepPlaying = askPlayAgain();
        }

        return buildSummary();
    }

    // ── FIXED: dedicated method using nextLine() ──────────────────────
    private boolean askPlayAgain() {
        System.out.print("\nSpin again? (y / n): ");
        try {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                input = scanner.nextLine().trim().toLowerCase();
            }
            return input.startsWith("y");
        } catch (Exception e) {
            return false;
        }
    }

    private void updateHotBoard(int pocket) {
        if (pocket == 0 || pocket == RouletteGame.DOUBLE_ZERO) {
            return;
        }

        hotBoard.addLast(pocket);

        if (hotBoard.size() > 10) {
            hotBoard.pollFirst();
        }

        hitCount.merge(pocket, 1, Integer::sum);
    }

    private void printHotBoard() {
        if (hotBoard.isEmpty()) {
            return;
        }

        System.out.print("\nLast results: ");

        hotBoard.forEach(p -> System.out.print(p + " "));

        int hot = hotBoard.stream()
                .max(Comparator.comparingInt(p -> hitCount.getOrDefault(p, 0)))
                .orElse(7);

        System.out.printf("%nHottest this session: %d <- bet it!%n%n", hot);
    }

    private void printMartingaleTip(double lostBet, double balance) {
        double doubleBet = lostBet * 2;

        if (doubleBet <= balance * BET_CAP_PCT) {
            System.out.printf(
                    "%nDealer tip: bet $%.2f on %s.%n",
                    doubleBet,
                    lastBetParam);
        }
    }

    private String chooseBetTypeWithTimer() {
        printBetMenu();
        System.out.printf("%d seconds to decide! Choice: ", TIMER_SECS);

        int choice;

        try {
            choice = Integer.parseInt(scanner.next().trim());
        } catch (NumberFormatException e) {
            choice = -1;
        }

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

    private boolean confirmQuit() {
        double net = casinoAccount.getBalance() - startingBalance;

        if (net < 0) {
            System.out.printf(
                    "%nYou're down $%.2f. Leave now? (y/n): ",
                    Math.abs(net));
        } else {
            System.out.print("\nCash out? (y/n): ");
        }

        return scanner.next().trim().toLowerCase().startsWith("y");
    }

    private void printWinFanfare(double profit, double payout) {
        System.out.println("\n==================================");
        System.out.printf("WINNER! +$%.2f%n", profit);
        System.out.printf("Payout: $%.2f%n", payout);
        System.out.println("==================================");
    }

    private void printBalanceLine(double balance, boolean showDelta, double delta) {
        if (showDelta && delta > 0) {
            System.out.printf("+$%.2f -> NEW BALANCE: $%.2f%n", delta, balance);
        } else {
            System.out.printf("%nBalance: $%.2f%n", balance);
        }
    }

    private void printBanner() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("           LUCKY SPIN ROULETTE          ");
        System.out.println("        American Wheel 0 00 1-36        ");
        System.out.println("========================================");
    }

    private void printBetMenu() {
        System.out.println();
        System.out.println("1. Straight  exact number");
        System.out.println("2. Split     two numbers");
        System.out.println("3. Street    row of 3");
        System.out.println("4. Corner    block of 4");
        System.out.println("5. Dozen     1-12 / 13-24 / 25-36");
        System.out.println("6. Column    column 1/2/3");
        System.out.println("7. Color     red/black");
        System.out.println("8. Parity    odd/even");
        System.out.println("9. High/Low  low/high");
        System.out.println("0. Quit");
    }

    private double chooseStake(double balance) {
        double maxBet = Math.floor(balance * BET_CAP_PCT * 100) / 100;

        System.out.printf("Bet [$%.2f - $%.2f]: $", MIN_BET, maxBet);

        double stake;

        try {
            stake = Double.parseDouble(scanner.next().trim());
        } catch (NumberFormatException e) {
            return -1;
        }

        if (stake < MIN_BET) {
            System.out.printf("Min bet is $%.2f%n", MIN_BET);
            return chooseStake(balance);
        }

        if (stake > maxBet) {
            System.out.printf("Table max for you is $%.2f%n", maxBet);
            return chooseStake(balance);
        }

        return stake;
    }

    private String chooseBetParam(String betType) {
        switch (betType) {
            case "straight":
                System.out.print("Number (0-36 or 00): ");
                break;
            case "split":
                System.out.print("Two numbers e.g. 14,15: ");
                break;
            case "street":
                System.out.print("First of row e.g. 1, 4, 7: ");
                break;
            case "corner":
                System.out.print("Top-left of block e.g. 11: ");
                break;
            case "dozen":
                System.out.print("Dozen 1, 2, or 3: ");
                break;
            case "column":
                System.out.print("Column 1, 2, or 3: ");
                break;
            case "color":
                System.out.print("Color red or black: ");
                break;
            case "parity":
                System.out.print("Odd or even: ");
                break;
            case "half":
                System.out.print("Low or high: ");
                break;
            default:
                return null;
        }

        return scanner.next().trim().toLowerCase();
    }

    private void animateSpin() {
        String[] frames = { "|", "/", "-", "\\" };

        for (int i = 0; i < 24; i++) {
            System.out.printf("\rSpinning %s", frames[i % 4]);

            try {
                Thread.sleep(80);
            } catch (InterruptedException ignored) {
            }
        }

        System.out.println("\rThe ball drops...     ");

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
                spinCount,
                totalWagered,
                net >= 0 ? "+" : "-",
                Math.abs(net),
                finalBalance);

        System.out.println("\n" + summary);

        return summary;
    }
}