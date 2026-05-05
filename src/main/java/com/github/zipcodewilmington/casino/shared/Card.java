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

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
