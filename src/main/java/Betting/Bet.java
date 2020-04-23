package Betting;

/**
 * Class to represent a bet made by a user.
 * @author sboughan
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

  /**
   * Calculates the change in user rating given the result of the bet
   * and updates that field in the bet.
   * @param c The actual change that occurred
   */
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

/**
 * Default bet constructor.
 * @param user ID of the user woh made the bet
 * @param percentChange the change in percentage the user thought would occur
 * @param champion the champion whose change the user bet on
 * @param gainFunction the function to be used to calculate the user's gain/loss
 */
  public Bet(String user, Double percentChange, String champion, GainFunction gainFunction) {
    this.userID = user;
    this.percentChangePredicted = percentChange;
    this.category = champion;
    this.gainFunction = gainFunction;
  }
}