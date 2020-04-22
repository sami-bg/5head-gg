package Betting;

public final class Bet {
  private final String userID;
  private final Double percentChangePredicted;
  private final String category;
  private final GainFunction gainFunction;
  private Double change = 0.0;
  private Double percentChangeActual;

  public String getCategory() {
    return category;
  }

  protected void calculateChange(Double c) {
    this.percentChangeActual = c;
    this.change = gainFunction.calculateGain(c, this);
  }

  public String getUserID() {
    return userID;
  }

  public Double getPercentChangePredicted() {
    return percentChangePredicted;
  }

  public Bet(String user, Double percentChange, String champion, GainFunction gainFunction) {
    this.userID = user;
    this.percentChangePredicted = percentChange;
    this.category = champion;
    this.gainFunction = gainFunction;
  }
}