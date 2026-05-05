package com.github.zipcodewilmington.casino.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A standard 52-card deck (no jokers).
 *
 * Construction creates a fresh, in-order deck. Call shuffle() before
 * dealing. draw() pulls one card off the top and removes it from the
 * deck. When you've drawn enough that you might run out, call reset()
 * (which refills to 52) followed by shuffle().
 *
 * draw() throws IllegalStateException on an empty deck so the calling
 * game can decide whether to reset/reshuffle or end the round.
 */
public class Deck {

    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        reset();
    }

    /**
     * Refill the deck with all 52 standard cards in order.
     * Does NOT shuffle — call shuffle() afterward if you want a random order.
     */
    public void reset() {
        cards.clear();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    /** Randomize the order of the remaining cards. */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Remove and return the top card of the deck.
     * @throws IllegalStateException if the deck is empty
     */
    
    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Cannot draw from an empty deck");
        }
        // remove from the end of the list — it's O(1) instead of O(n) from the front
        return cards.remove(cards.size() - 1);
    }

    public int cardsRemaining() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
