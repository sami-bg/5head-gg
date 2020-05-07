package Betting;

import RiotAPI.ChampConsts;
import org.junit.Test;

import static org.junit.Assert.*;

public class BettingSessionTest {

	@Test
	public void testBetSesh() {
		final Bet testBet = new Bet("hash", "testUser", 51, 0.51, "Teemo", new TestGainFunction(), "win", "10.9");
		final Bet testBet1 = new Bet("hash1", "testUser1", 52, 0.51, "Teemo", new TestGainFunction(), "win", "10.9");
		final Bet testBet2 = new Bet("hash2", "testUser", 51, 0.51, "Taric", new TestGainFunction(), "ban:", "10.9");
		BettingSession testBetSesh = new BettingSession("winRate", ChampConsts.getChampNames());
		testBetSesh.addBet(testBet);
		assertEquals(testBetSesh.getBetsFromUserID("testUser").get(0).getUserID(), "testUser");
		assertEquals(testBetSesh.getBetsFromUserID("testUser").get(0).getCategory(), "Teemo");
		testBetSesh.addBet(testBet1);
		assertEquals(testBetSesh.getMapOfChampionToBets().get("Teemo").get(0).getUserID(), "testUser");
		assertEquals(testBetSesh.getMapOfChampionToBets().get("Teemo").get(1).getUserID(), "testUser1");
		testBetSesh.addBet(testBet2);
		assertEquals(testBetSesh.getBetsFromUserID("testUser").get(1).getCategory(), "Taric");
		assertEquals(testBetSesh.getMapOfChampionToBets().get("Taric").get(0).getUserID(), "testUser");
		assertTrue(testBetSesh.getUsers().contains("testUser"));
		assertTrue(testBetSesh.getUsers().contains("testUser1"));
		assertTrue(!testBetSesh.getUsers().contains("hash"));
		assertThrows(NullPointerException.class, () -> testBetSesh.broadcast(0.51, 0.51, "Eevee"));
		testBetSesh.broadcast(0.51, 0.51, "Taric");
		assertEquals(testBetSesh.getMapOfChampionToBets().get("Taric").get(0).getGain(), 0.51 * -1, 0.001);
		assertEquals(testBetSesh.getType(), "winRate");
		assertThrows(NullPointerException.class, () -> testBetSesh.getBetsFromUserID("testUser2").get(0));
		testBetSesh.resetSession();
		assertThrows(IndexOutOfBoundsException.class, () -> testBetSesh.getMapOfChampionToBets().get("Taric").get(0));
		assertThrows(IndexOutOfBoundsException.class, () -> testBetSesh.getBetsFromUserID("testUser").get(0));
		assertThrows(NullPointerException.class, () -> testBetSesh.getBetsFromUserID("testUser2").get(0));
		assertThrows(NullPointerException.class, () -> testBetSesh.getMapOfChampionToBets().get("Eevee").get(0));

	}
}
