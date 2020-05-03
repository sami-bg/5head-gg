package main.java.Betting;

public class TestGainFunction implements GainFunction {
	public Double calculateGain(Double actual, Bet b){
	return actual - b.getPercentChangePredicted();
}
}