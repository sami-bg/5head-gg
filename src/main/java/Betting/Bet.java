package Betting;

/**
*
*/

public final class Bet {
  private final String userID;
  private final Double percentChangePredicted;
  private final String category;
  private final GainFunction gainFunction;
  private Double change = 0.0;
  private Double percentChangeActual;

  /**
   * Category getter.
   * @return the category (i.e. champion) the bet was put in.
   */

  public String getCategory() {
    return category;
  }

  protected void calculateChange(Double c) {
    this.percentChangeActual = c;
    this.change = gainFunction.calculateGain(c, this);
  }

  /**
   * User ID getter.
   * @return the ID of the user who made the bet.
   */
  public String getUserID() {
    return userID;
  }

  /**
   * Predicted percentage getter.
   * @return the percentage that the user predicted.
   */
  public Double getPercentChangePredicted() {
    return percentChangePredicted;
  }

  /**
   * User reputation change getter for testing purposes.
   * @return the multiplier of the reputation of the user after the bet.
   */
  protected Double getChange() {
    return change;
  }


  public Bet(String user, Double percentChange, String champion, GainFunction gainFunction) {
    this.userID = user;
    this.percentChangePredicted = percentChange;
    this.category = champion;
    this.gainFunction = gainFunction;
  }
}