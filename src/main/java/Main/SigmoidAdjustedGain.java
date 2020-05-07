package Main;

import Betting.Bet;
import Betting.GainFunction;
import org.apache.commons.math3.analysis.function.Logistic;

public class SigmoidAdjustedGain implements GainFunction {

  private Logistic logisticFunction;
  /**
   * Multiplies reputation by maxGainMult when predicted change within lowerBound of true change.
   * Multiplies reputation by minGainMult when predicted change outside upperBound of true change.
   * @param maxGainMult - Highest gain multiplier possible.
   * @param minGainMult - Lowest gain multiplier possible.
   * @param upperBound - Upper bound before gain is minGainMult.
   * @param lowerBound - Lower bound before gain is maxGainMult.
   */
  //We account for popularity elsewhere. this class returns raw main multiplier.
  public SigmoidAdjustedGain(Double maxGainMult, Double minGainMult, Double upperBound, Double lowerBound) {
    /**
     k - If b > 0, value of the function for x going towards +∞. If b < 0, value of the function for x going towards -∞.
     m - Abscissa of maximum growth - point at which gradient is highest
     b - Growth rate.
     q - Parameter that affects the position of the curve along the ordinate axis.
     a - If b > 0, value of the function for x going towards -∞. If b < 0, value of the function for x going towards +∞.
     n - Parameter that affects near which asymptote the maximum growth occurs.
     */
    double k = maxGainMult;
    double m = 0.3 * (upperBound + lowerBound);
    double b = -4;
    double q = 1.0;
    double a = minGainMult;
    double n = 1;
    this.logisticFunction = new Logistic(k, m, b, q, a, n);
  }

  @Override
  public Double calculateGain(Double change, Bet b) {
    Double distance = Math.abs(b.getPercentChangePredicted() - change);
    return logisticFunction.value(distance);
  }

  public static Integer calculateSigmoidReputationChange(Integer reputation, Double multiplier) {
    return (int) (reputation * multiplier);
  }
}
