package com.github.zipcodewilmington.casino.games.war;

import com.github.zipcodewilmington.casino.CasinoAccount;
import com.github.zipcodewilmington.casino.shared.Card;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WarPlayerTest {

    @Test
    public void getArcadeAccountReturnsAccountPassedToConstructor() {
        CasinoAccount account = new CasinoAccount();
        WarPlayer player = new WarPlayer(account);
        assertSame(account, player.getArcadeAccount(),
                "Spec requires PlayerInterface to reference its CasinoAccount");
    }

    @Test
    public void newPlayerHasZeroBet() {
        WarPlayer player = new WarPlayer(new CasinoAccount());
        assertEquals(0L, player.getCurrentBet());
    }

    @Test
    public void setCurrentBetUpdatesField() {
        WarPlayer player = new WarPlayer(new CasinoAccount());
        player.setCurrentBet(50L);
        assertEquals(50L, player.getCurrentBet());
    }

    @Test
    public void newPlayerHasNoCurrentCard() {
        WarPlayer player = new WarPlayer(new CasinoAccount());
        assertNull(player.getCurrentCard());
    }

    @Test
    public void setCurrentCardUpdatesField() {
        WarPlayer player = new WarPlayer(new CasinoAccount());
        Card ace = new Card(Card.Rank.ACE, Card.Suit.SPADES);
        player.setCurrentCard(ace);
        assertSame(ace, player.getCurrentCard());
    }

    @Test
    public void playReturnsNullByDesign() {
        // WarGame drives the play loop, not the player.
        WarPlayer player = new WarPlayer(new CasinoAccount());
        assertNull(player.play());
    }
}
