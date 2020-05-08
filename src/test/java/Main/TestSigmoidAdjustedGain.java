package Main;

import Betting.Bet;
import Betting.GainFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.checkerframework.checker.nullness.Opt;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.math3.analysis.function.*;

import javax.swing.text.html.Option;

import static org.junit.Assert.*;
import java.util.Optional;

public class TestSigmoidAdjustedGain {

  private static SigmoidAdjustedGain sigmoid;
  private static Double UPPER_BOUND;
  private static Double LOWER_BOUND;
  private static Double MAX_GAIN_MULT;
  private static Double LOWEST_GAIN_MULT;
  private static Bet bet;

  @Before
  public void setUp() {
    LOWEST_GAIN_MULT = 0.3;
    MAX_GAIN_MULT = 3.0;
    UPPER_BOUND = 2.0;
    LOWER_BOUND = 0.2;
    sigmoid = new SigmoidAdjustedGain(LOWEST_GAIN_MULT, MAX_GAIN_MULT, UPPER_BOUND, LOWER_BOUND);
    double k = MAX_GAIN_MULT; // highest multiplier
    double m = 0.3 * (UPPER_BOUND + LOWER_BOUND);
    double b = -4; // direction of slope? upwards in our case (from L -> R)
    double q = 1.0; // DON"T CHANGE
    double a = LOWEST_GAIN_MULT; // Lowest multiplier
    double n = 1; // i dont know how this should look like. DON"T CHANGE
    Logistic apacheLogistic = new Logistic(k, m, b, q, a, n);
    /*
    public Bet(String betID, String userID, int rep, Double percentChange, String champion, GainFunction gainFunction, String betType, String patch)
     */
    bet = new Bet("0", "0", 100, 0.0, "NoChamp", sigmoid, "noType", "patch");
  }
  @Test
  public void testSigmoidGain() {
    Double delta = 0.4;
    assertEquals(Optional.of(SigmoidAdjustedGain.calculateSigmoidReputationChange(5, 1.5)), Optional.of(7));
    Double sigmoidMultiplierMin = sigmoid.calculateGain(UPPER_BOUND, bet);
    assertEquals(sigmoidMultiplierMin, MAX_GAIN_MULT, delta);
    Double sigmoidMultiplierMax = sigmoid.calculateGain(LOWER_BOUND, bet);
    assertEquals(sigmoidMultiplierMax, LOWEST_GAIN_MULT, delta);
    assertEquals(sigmoid.calculateGain(2.0, bet), MAX_GAIN_MULT, delta);
  }
}
