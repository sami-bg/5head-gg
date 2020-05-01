package main;

import java.util.List;

public class Patch {
	
	private final String id;
	private final List<String> users;
	
	public Patch(String id, List<String> users) {
		this.id =id;
		this.users = users;
	}
	
	public List<String> getUsers() {
		return users;
	}

}
