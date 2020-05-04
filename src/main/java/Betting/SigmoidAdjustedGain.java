package Betting;

public class SigmoidAdjustedGain implements GainFunction {

  private final Double upperBound;
  private final Double lowerBound;
  private Double maxAdjustment = 0.75;
  private Double maxGainMult = 1.5;

  /**
   *
   * @param maxGainMult - Highest gain multiplier possible. Defaults to 1.5:
   *                    calculateGain returns 1.5 when within some epsilon of
   *                    actual.
   * @param maxAdjustment - Highest gain reduction possible. Defaults to 0.75.
   *                      This is the maximum number that is subtracted from maxGainMult
   *                      in case of worst accuracy. -0.75 is subtracted from default of 1.5
   *                      when not within some delta of actual.
   * @param upperBound - Upper bound before sigmoid evaluates to maxAdjustment:
   *                   This is the farthest distance from actual before maxAdjustment is removed
   *                   from maxGainMult.
   * @param lowerBound - Lower bound before gain reduction evaluates to 0:
   *                   This is the distance within actual you need to be so that
   *                   you get the maximum gain multiplier.
   *
   */
  //We account for popularity elsewhere. this class returns raw main multiplier.
  public SigmoidAdjustedGain(Double maxGainMult, Double maxAdjustment, Double upperBound, Double lowerBound) {
    this.maxGainMult = maxGainMult;
    this.upperBound = upperBound;
    this.lowerBound = lowerBound;
    this.maxAdjustment = maxAdjustment;
  }

  @Override
  public Double calculateGain(Double change, Bet b) {
    return maxGainMult - calculateAdjustment(b.getPercentChangePredicted() - change, 1.0);
  }

  /**
   *
   * @param distance - distance from actual bet result.
   * @param steepnessMultiplier - a multiplier to scale the steepness of adjusted sigmoid.
   *                            Original bounds are -7 to 7, steepness is a linear multiplier
   *                            of those bounds.
   * @return - adjustment that is subtracted from maxGainMult.
   * In best case scenario, this is 0. In worst case, this is maxAdjustment.
   */
  private Double calculateAdjustment(double distance, Double steepnessMultiplier) {
    double range = Math.abs(this.upperBound - this.lowerBound);
    Double boundsPreMultiplication = 7.0;
    //Sigmoid:
    return maxAdjustment / (1 + Math.exp(-1 * (steepnessMultiplier * boundsPreMultiplication / range) * (distance + upperBound)));
  }
}
