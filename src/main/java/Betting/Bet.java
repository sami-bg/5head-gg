package main.java.Betting;

public final class Bet {
private String user;
private int rep;
private float percentChange;
private String champion;

public Bet(String user, int rep, float percentChange, String champion) {
	this.user = user;
	this.rep = rep;
	this.percentChange = percentChange;
	this.champion = champion;
}
}
