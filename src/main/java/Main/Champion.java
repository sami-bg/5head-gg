package Main;

import java.util.List;

public class Champion {

	public String name;
	public List<String> winRate;
	public List<String> pickRate;
	public List<String> banRate;
	

	/**
	 * Default constructor.
	 * @param name
	 * @param winRate
	 * @param banRate
	 * @param pickRate
	 */
	public Champion(String name, List<String> winRate, List<String> banRate, List<String> pickRate) {
		this.name = name;
		this.winRate = winRate;
		this.banRate = banRate;
		this.pickRate = pickRate;
	}
}
