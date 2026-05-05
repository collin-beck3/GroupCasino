package com.github.zipcodewilmington.casino.shared;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    @Test
    public void newDeckHas52Cards() {
        Deck deck = new Deck();
        assertEquals(52, deck.cardsRemaining());
    }

    @Test
    public void newDeckIsNotEmpty() {
        Deck deck = new Deck();
        assertFalse(deck.isEmpty());
    }

    @Test
    public void drawReducesDeckByOne() {
        Deck deck = new Deck();
        deck.draw();
        assertEquals(51, deck.cardsRemaining());
    }

    @Test
    public void canDrawAll52Cards() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            deck.draw();
        }
        assertEquals(0, deck.cardsRemaining());
        assertTrue(deck.isEmpty());
    }

    @Test
    public void deckContainsAll52UniqueCards() {
        Deck deck = new Deck();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            Card card = deck.draw();
            seen.add(card.getRank() + "-" + card.getSuit());
        }
        assertEquals(52, seen.size(), "Every drawn card should be unique");
    }

    @Test
    public void drawFromEmptyDeckThrows() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            deck.draw();
        }
        assertThrows(IllegalStateException.class, deck::draw);
    }

    @Test
    public void shuffleKeepsAllCards() {
        Deck deck = new Deck();
        deck.shuffle();
        assertEquals(52, deck.cardsRemaining());
    }

    @Test
    public void resetRefillsDeckAfterDraws() {
        Deck deck = new Deck();
        deck.draw();
        deck.draw();
        deck.draw();
        deck.reset();
        assertEquals(52, deck.cardsRemaining());
    }

    @Test
    public void resetWorksOnEmptyDeck() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            deck.draw();
        }
        deck.reset();
        assertEquals(52, deck.cardsRemaining());
        assertFalse(deck.isEmpty());
    }
}
