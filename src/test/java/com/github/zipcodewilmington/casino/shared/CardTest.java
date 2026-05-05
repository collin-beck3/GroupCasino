package com.github.zipcodewilmington.casino.shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    public void cardStoresRankAndSuit() {
        Card card = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        assertEquals(Card.Rank.ACE, card.getRank());
        assertEquals(Card.Suit.HEARTS, card.getSuit());
    }

    @Test
    public void higherRankBeatsLowerRank() {
        Card king = new Card(Card.Rank.KING, Card.Suit.HEARTS);
        Card two = new Card(Card.Rank.TWO, Card.Suit.SPADES);
        assertTrue(king.compareTo(two) > 0);
    }

    @Test
    public void lowerRankLosesToHigherRank() {
        Card three = new Card(Card.Rank.THREE, Card.Suit.HEARTS);
        Card jack = new Card(Card.Rank.JACK, Card.Suit.CLUBS);
        assertTrue(three.compareTo(jack) < 0);
    }

    @Test
    public void sameRankReturnsZeroEvenWithDifferentSuits() {
        Card heartsTen = new Card(Card.Rank.TEN, Card.Suit.HEARTS);
        Card spadesTen = new Card(Card.Rank.TEN, Card.Suit.SPADES);
        assertEquals(0, heartsTen.compareTo(spadesTen));
    }

    @Test
    public void aceIsHighest() {
        Card ace = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        Card king = new Card(Card.Rank.KING, Card.Suit.HEARTS);
        assertTrue(ace.compareTo(king) > 0);
    }

    @Test
    public void twoIsLowest() {
        Card two = new Card(Card.Rank.TWO, Card.Suit.CLUBS);
        Card three = new Card(Card.Rank.THREE, Card.Suit.CLUBS);
        assertTrue(two.compareTo(three) < 0);
    }

    @Test
    public void rankValuesAreCorrect() {
        assertEquals(2, Card.Rank.TWO.getValue());
        assertEquals(11, Card.Rank.JACK.getValue());
        assertEquals(14, Card.Rank.ACE.getValue());
    }

    @Test
    public void toStringIncludesRankAndSuit() {
        Card card = new Card(Card.Rank.QUEEN, Card.Suit.DIAMONDS);
        String result = card.toString();
        assertTrue(result.contains("QUEEN"));
        assertTrue(result.contains("DIAMONDS"));
    }
}
