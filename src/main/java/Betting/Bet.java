package main.java.Betting;

import main.java.User;

public final class Bet {
private User user;
private int rep;
private float percentChange;
private String champion;

public Bet(User user, int rep, float percentChange, String champion) {
	this.user = user;
	this.rep = rep;
	this.percentChange = percentChange;
	this.champion = champion;
}
}
