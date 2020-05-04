package main.java.Betting;

/**
 * Interface that represents the function to calculate 
 * how much a bet will gain/lose
 * @author sboughan
 *
 */
public interface GainFunction {
	/**
	 * The algorithm for the particular kind of GainFunction,
	 * which can be defined by the user
	 * @param actual - the actual CHANGE in rate.
	 * @param b the bet to calculate the gain/loss
	 * @return a double representing the multiplier
	 * to the user's reputation
	 */
    Double calculateGain(Double actual, Bet b);
}
