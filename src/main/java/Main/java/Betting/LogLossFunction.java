package Main.java.Betting;

public class LogLossFunction implements GainFunction {
//sample log loss function that returns the multiplier of the bet,
//with a perfect accuracy bet returning 2x,
//a bet that is off by 5.1% returning 1x,
//and a bet that is more than 10% off returning 0.
  public Double calculateGain(Double actual, Bet b){
	double predicted = b.getPercentChangePredicted();
	if(predicted == actual) {
		return 2.0;
		} 
	else if(Math.abs(actual - predicted) > 0.1) {
		return 0.0;
		} else {
	return 2.0 + (19.0 * Math.log(1.0 - Math.abs(actual - predicted)));
		}
	}
}
