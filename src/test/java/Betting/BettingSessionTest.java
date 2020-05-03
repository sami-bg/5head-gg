package test.java.Betting;

import org.junit.Test;

import main.java.Betting.Bet;
import main.java.Betting.BettingSession;
import main.java.Betting.TestGainFunction;

import static org.junit.Assert.assertEquals;

public class BettingSessionTest {

	@Test
	public void testBetSesh() {
		final Bet testBet = new Bet("hash", "testUser", 51,
				0.51, "Teemo", new TestGainFunction(), "win");
		final Bet testBet1 = new Bet("hash1", "testUser1", 52, 0.51,
				"Teemo", new TestGainFunction(), "win");
		final Bet testBet2 = new Bet("hash2", "testUser", 51, 0.51,
				"Taric", new TestGainFunction(), "ban:");
		BettingSession testBetSesh = new BettingSession("winRate");
		testBetSesh.addBet(testBet);
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
