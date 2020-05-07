package Main;

import Betting.Bet;
import Betting.GainFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.math3.analysis.function.*;

public class TestSigmoidAdjustedGain {

  private static SigmoidAdjustedGain sigmoid;
  private static Sigmoid apacheSigmoid;
  private static Logistic apacheLogistic;
  private static NormalDistribution apacheBell;
  @Before
  public void setUp() {
    Double LOWEST_GAIN_MULT = 0.3;
    Double MAX_GAIN_MULT = 3.0;
    Double UPPER_BOUND = 2.0;
    Double LOWER_BOUND = 0.2;
    sigmoid = new SigmoidAdjustedGain(LOWEST_GAIN_MULT, MAX_GAIN_MULT, UPPER_BOUND, LOWER_BOUND);
    apacheSigmoid = new Sigmoid(0, MAX_GAIN_MULT);
    double k = MAX_GAIN_MULT; // highest multiplier
    double m = 0.3 * (UPPER_BOUND + LOWER_BOUND);
    double b = -4; // direction of slope? upwards in our case (from L -> R)
    double q = 1.0; // DON"T CHANGE
    double a = LOWEST_GAIN_MULT; // Lowest multiplier
    double n = 1; // i dont know how this should look like. DON"T CHANGE
    apacheLogistic = new Logistic(k, m, b, q, a, n);
    /*
    public Bet(String betID, String userID, int rep, Double percentChange, String champion, GainFunction gainFunction, String betType, String patch)
     */
//    Bet b = new Bet("0", "0",);
  }
  @Test
  public void testSigmoidGain() {

    System.out.println(apacheLogistic.value(0.2));
    System.out.println(apacheLogistic.value(0.4));
    System.out.println(apacheLogistic.value(0.8));
    System.out.println(apacheLogistic.value(1.2));
    System.out.println(apacheLogistic.value(1.6));
    System.out.println(apacheLogistic.value(2.0));

    System.out.println(apacheLogistic.value(10.0));
    System.out.println(apacheLogistic.value(50.0));




  }
}
