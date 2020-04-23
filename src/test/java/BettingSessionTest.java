package Betting;

import Betting.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BettingSessionTest {

	@Test
	public void testBetSesh() {
		final Bet testBet = new Bet("testUser", 0.51, "Teemo", new TestGainFunction());
		final Bet testBet1 = new Bet("testUser1", 0.51, "Teemo", new TestGainFunction());
		final Bet testBet2 = new Bet("testUser", 0.51, "Taric", new TestGainFunction());
		BettingSession testBetSesh = new BettingSession("winRate");
		testBetSesh.addBet(new Bet("testUser", 0.51, "Teemo", new TestGainFunction()));
		assertEquals(testBetSesh.getBetsFromUserID("testUser").get(0).getUserID(), "testUser");
		assertEquals(testBetSesh.getBetsFromUserID("testUser").get(0).getCategory(), "Teemo");
		testBetSesh.addBet(testBet1);
		assertEquals(testBetSesh.getMapOfChampionToBets().get("Teemo").get(0).getUserID(), "testUser");
		assertEquals(testBetSesh.getMapOfChampionToBets().get("Teemo").get(1).getUserID(), "testUser1");
		testBetSesh.addBet(testBet2);
		assertEquals(testBetSesh.getBetsFromUserID("testUser").get(1).getCategory(), "Taric");
		assertEquals(testBetSesh.getMapOfChampionToBets().get("Taric").get(0).getUserID(), "testUser");
	}
}
