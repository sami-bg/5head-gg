package main.java;

import java.util.List;

public class Champion {

	public String name;
	public float winRate;
	public float pickRate;
	public float banRate;
	
	private List<Double> pastWinRate; 
	
	public Champion(List<String> winRate, List<String> banRate, List<String> pickRate) {
		
	}
}
