package main.java;

import java.util.List;

import main.java.Database.DatabaseHandler;
import main.java.Betting.GainFunction;
import main.java.Betting.Bet;

public class User {
private final String id;
private int reputation;
private final String username;
private final String email;
private final String authentication;


public User(String id, String username, int reputation, String email, String authentication) {
	this.authentication = authentication;
	this.email = email;
	this.username = username;
	this.id = id;
	this.reputation = reputation;
}

public User(List<String> dataFields) {
	this.id = dataFields.get(0);
	this.username = dataFields.get(1);
	this.reputation = Integer.parseInt(dataFields.get(2));
	this.email = dataFields.get(3);
	this.authentication = dataFields.get(4);
}


public void submitBet(int rep, String percentChange, String champion, String stat) {
	//add to bet database
	String betID = "";
	//think of a way to generate unique ids
	DatabaseHandler.createNewBet(betID, id, champion, stat, percentChange, String.valueOf(rep));
	
	
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
