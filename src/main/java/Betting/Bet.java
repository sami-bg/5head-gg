package main.java.Betting;

import java.util.List;

/**
 * Class to represent a bet made by a user.
 * @author sboughan
 *
 */
public final class Bet {
  private final int repWagered;
  private final Double percentChangePredicted;
  private final String category;
  private final GainFunction gainFunction;
  private Double gain = 0.0;
  private Double percentChangeActual;
private String betType;
private String betID;

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
   * @param c The actual CHANGE that occurred
   */
  protected void calculateChange(Double c) {
    this.percentChangeActual = c;
    this.gain = gainFunction.calculateGain(c, this);
  }

  /**
   * The reputation wagered by a user
   * @return the ID of the user who made the bet.
   */
  public int getRepWagered() {
    return repWagered;
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
  protected Double getGain() {
    return gain;
  }

/**
 * Default bet constructor.
 * @param rep  reputation wagered
 * @param percentChange the change in percentage the user thought would occur
 * @param champion the champion whose change the user bet on
 * @param gainFunction the function to be used to calculate the user's gain/loss
 */
  public Bet(String betID, int rep, Double percentChange, String champion, GainFunction gainFunction, String betType) {
	this.gainFunction = null;
	this.betID = betID;
    this.repWagered = rep;
    this.percentChangePredicted = percentChange;
    this.category = champion;
    this.gainFunction = gainFunction;
    this.betType = betType;
  }
  
  public Bet(List<String> dataFields) {

	this.betID = dataFields.get(0);
	this.category = dataFields.get(2);
	this.betType = dataFields.get(3);
	this.percentChangePredicted = Double.parseDouble(dataFields.get(4));
	this.repWagered = Integer.parseInt(dataFields.get(5));
  }
}

