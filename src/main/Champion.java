package main;

import java.util.List;

public class Champion {

	public String name;
	public List<String> winRate;
	public List<String> pickRate;
	public List<String> banRate;
	
	private List<Double> pastWinRate;

	/**
	 * Default constructor.
	 * @param winRate
	 * @param banRate
	 * @param pickRate
	 */
	public Champion(List<String> winRate, List<String> banRate, List<String> pickRate) {
		this.winRate = winRate;
		this.banRate = banRate;
		this.pickRate = pickRate;
	}
}
