package Betting;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BetTest {

@Test
public void testBet(){
	Bet testBet = new Bet("hash", "testUser", 52, 0.52,
			"Aatrox", new TestGainFunction(), "testType");
	assertEquals(testBet.getCategory(), "Aatrox");
	assertEquals(testBet.getUserID(), "testUser");
	testBet.calculateChange(0.52);
	assertEquals(testBet.getGain(), (Double) 0.0);
}

}