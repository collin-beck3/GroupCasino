package com.github.zipcodewilmington.casino.shared;

/**
 * A standard playing card with a rank (TWO–ACE) and a suit.
 *
 * Cards are immutable — once created, rank and suit don't change.
 * Cards implement Comparable<Card> so two cards can be compared with
 * playerCard.compareTo(dealerCard). Comparison is by rank only;
 * suits don't break ties (Casino War treats a suit-tie as a real tie).
 *
 * Aces are HIGH (value 14). If we ever add BlackJack, that game will
 * need its own ace-low/ace-high logic — don't change the value here.
 */

public class Card implements Comparable<Card> {

    /** The four suits. Order doesn't matter for ranking. */
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    /**
     * Card ranks from low to high. Each rank carries an int value
     * that compareTo() uses to decide which card wins.
     */

    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
        EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13), ACE(14);

        private final int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    /**
     * Compare this card to another by rank only.
     * Returns negative if this < other, zero if equal rank, positive if this > other.
     */

    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.rank.getValue(), other.rank.getValue());
    }

    /**
     * Returns the single- or double-character label used on the card face.
     * TWO through NINE → "2"–"9", TEN → "10", JACK → "J", QUEEN → "Q",
     * KING → "K", ACE → "A".
     */
    public String rankSymbol() {
        switch (rank) {
            case ACE:   return "A";
            case KING:  return "K";
            case QUEEN: return "Q";
            case JACK:  return "J";
            case TEN:   return "10";
            default:    return String.valueOf(rank.getValue());
        }
    }

    /**
     * Returns the Unicode suit glyph for this card's suit.
     * Hearts → ♥, Diamonds → ♦, Clubs → ♣, Spades → ♠.
     */
    public String suitSymbol() {
        switch (suit) {
            case HEARTS:   return "♥"; // ♥
            case DIAMONDS: return "♦"; // ♦
            case CLUBS:    return "♣"; // ♣
            case SPADES:   return "♠"; // ♠
            default:       return "?";
        }
    }

    /**
     * Returns a 7-element String array representing the card as ASCII art.
     * Every row is exactly 11 characters wide.
     *
     * <pre>
     * .---------.
     * | A       |
     * |         |
     * |    ♥    |
     * |         |
     * |       A |
     * '---------'
     * </pre>
     *
     * For a two-character rank (10):
     *
     * <pre>
     * .---------.
     * | 10      |
     * |         |
     * |    ♦    |
     * |         |
     * |      10 |
     * '---------'
     * </pre>
     */
    public String[] toAsciiArt() {
        String r = rankSymbol();  // 1 or 2 chars
        String s = suitSymbol();  // 1 Unicode char

        // Each content row is 9 chars wide (between the two '|' borders).
        // Top-left corner: rank flushed left inside a 9-char field (with 1-space margin).
        // Bottom-right corner: rank flushed right inside a 9-char field (with 1-space margin).
        // The '| ' prefix and ' |' suffix each add 1 padding space, so topRank/botRank
        // must be exactly 7 chars so that  | (1) + space (1) + 7 + space (1) + | (1) = 11.
        String topRank; // 7 chars, rank at left
        String botRank; // 7 chars, rank at right
        if (r.length() == 2) {
            topRank = r + "     "; // e.g. "10     "
            botRank = "     " + r; // e.g. "     10"
        } else {
            topRank = r + "      "; // e.g. "A      "
            botRank = "      " + r; // e.g. "      A"
        }

        // Centre row: suit glyph centred in 9 chars → 4 spaces + glyph + 4 spaces
        String suitRow = "    " + s + "    "; // exactly 9 chars

        return new String[] {
            ".---------.",         // 11 chars
            "| " + topRank + " |", // 11 chars
            "|         |",         // 11 chars
            "|" + suitRow  + "|",  // 11 chars
            "|         |",         // 11 chars
            "| " + botRank + " |", // 11 chars
            "'---------'"          // 11 chars
        };
    }

    @Override
    public String toString() {
        // Keep the all-caps enum names so existing tests (which assert on
        // "QUEEN" and "DIAMONDS") continue to pass.  The game now uses
        // toAsciiArt() instead of toString() for on-screen rendering.
        return rank + " of " + suit;
    }
}
