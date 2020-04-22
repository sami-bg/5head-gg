package Betting;

public interface GainFunction {
  Double calculateGain(Double predicted, Bet b);
}
