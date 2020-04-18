package data;

public class User {
private final String id;
private int reputation;

public User(String id, int reputation) {
	this.id = id;
	this.reputation = reputation;
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
