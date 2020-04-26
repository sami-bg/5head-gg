package main.java;

import java.util.List;

import Betting.Bet;

public class User {
private final String id;
private int reputation;

public User(String id, int reputation) {
	this.id = id;
	this.reputation = reputation;
}

public User(List<String> dataFields) {
	id = dataFields.get(0);
}


public void submitBet(int rep, float percentChange, String champion, String stat) {
	//new Bet(this, rep, percentChange, champion);
	//add to bet database
}
public String getID() {
	return id;
}
/**
 * @return the reputation
 */
public int getReputation() {
	return reputation;
}
/**
 * @param reputation the reputation to set
 */
public void setReputation(int reputation) {
	this.reputation = reputation;
}

public void viewHistory() {
	
}
}
