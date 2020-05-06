package Betting;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BetTest {

@Test
public void testBet(){
	Bet testBet;
	testBet = new Bet("hash", "testUser", 52, 0.52,
			"Aatrox", new TestGainFunction(), "testType", "10.9");
	assertEquals(testBet.getCategory(), "Aatrox");
	assertEquals(testBet.getUserID(), "testUser");
	testBet.calculateChange(0.52);
	assertEquals(testBet.getGain(), (Double) 0.0);
}

@Test
	public void testBet1(){
	Bet testBet1;
	testBet1 = new Bet("hash", "testUser", 68, 1.365,
			"erg", new TestGainFunction(), "testType", "10.9");
	assertEquals(testBet1.getPercentChangePredicted(), (Double) 1.365);
	assertEquals(testBet1.getCategory(), "erg");
	assertEquals(testBet1.getGain(), (Double) 0.0);
	assertEquals(testBet1.getRepWagered(), 68);
	testBet1.calculateChange(0.52);
	assertEquals(testBet1.getGain() + 0.845, 0, 0.0001);
}

@Test
	public void testBet2(){
	Bet testBet2;
	testBet2 = new Bet(new TestGainFunction(), List.of("betID", "userID", "Kog'Maw", "ban", "-0.52", "-4", "10.9"));
	assertEquals(testBet2.getBetType(), "ban");
	testBet2.calculateChange(0.0);
	assertEquals(testBet2.getGain(), 0.52, 0.0001);
}

}