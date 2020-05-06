package Main;

import java.util.List;

public class User {
    private final String id;
    private int reputation;
    private final String username;
    private final String email;
    private final String authentication;

    /**
     * Constructor for User.
     * @param id The unique ID of the user
     * @param username The user name shown in the website and leaderboards
     * @param reputation The amount of reputation a user has
     * @param email The email of the user (not used)
     * @param authentication The password of the user
     */
    public User(String id, String username, int reputation, String email, String authentication) {
        this.authentication = authentication;
        this.email = email;
        this.username = username;
        this.id = id;
        this.reputation = reputation;
    }

    /**
     * Alternate constructor that uses a list that contains the data for the user.
     * @param dataFields List that contains the same data as the above constructor
     */
    public User(List<String> dataFields) {
        this.id = dataFields.get(0);
        this.username = dataFields.get(1);
        this.reputation = Integer.parseInt(dataFields.get(2));
        this.email = dataFields.get(3);
        this.authentication = dataFields.get(4);
    }

    /**
     * ID getter.
     *
     * @return ID of current user.
     */
    public String getID() {
        return id;
    }

    /**
     * Username getter.
     *
     * @return username of current user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Reputation getter.
     * @return the reputation
     */
    public int getReputation() {
        return reputation;
    }

    /**
     * Password getter (only used for testing purposes!)
     * @return the authentication
     */
    public String getAuth() {
        return authentication;
    }

    /**
     * Reputation setter.
     * @param reputation the reputation to set
     */
    public void setReputation(int reputation) {
        this.reputation = reputation;
    }


}
