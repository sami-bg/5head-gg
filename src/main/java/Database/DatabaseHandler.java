package Database;

import Betting.Bet;
import Main.SigmoidAdjustedGain;
import Main.Champion;
import Main.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHandler {

	private static Connection conn;
	private final List<String> prevFiles;

	private boolean isLocked;

	/**
	 * The constructor for the SqlDatabaseReader instantiates the list of previous
	 * files to be kept track of in case a bad file is read in.
	 */
	public DatabaseHandler() {
		prevFiles = new ArrayList<String>();
		isLocked = false;
	}

	/**
	 * This method reads in a new database.
	 *
	 * @param filename The path to the database file to be read in
	 */
	public void read(String filename) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("Read error: org.sqlite.JDBC not found");
		}
		String urlToDB = "jdbc:sqlite:" + filename;
		try {
			conn = DriverManager.getConnection(urlToDB);
		} catch (SQLException e) {
			System.out.println("SQLException when creating connection to database -- Database doesn't exist");
		}
	}

	/**
	 * This method queries the database. It exists so that other classes do not have
	 * to directly make calls to the database, but instead this can be done here.
	 *
	 * @param query The SQL Query which will be called to the database
	 * @param args  The argument which can be passed into the SQL call
	 * @return A list containing the elements from the sql call
	 */
	public static List<List<String>> queryData(String query, List<String> args) {
		List<List<String>> res;
		try {
			PreparedStatement prep = conn.prepareStatement(query);
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					prep.setString(i + 1, args.get(i));
				}
			}
			ResultSet result = prep.executeQuery();
			res = new ArrayList<>();
			while (result.next()) {
				List<String> row = new ArrayList<String>();
				for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
					row.add(result.getString(i));
				}
				System.out.println(row.toString());
				res.add(row);
			}
			prep.close();
			result.close();
		} catch (NullPointerException e) {
			System.out.println("ERROR: Database is not connected");
			return Arrays.asList(new ArrayList<String>());
		} catch (SQLException e) {
			System.out.println("Error: SQL connection error");
			return Arrays.asList(new ArrayList<String>());
		}

		return res;
	}

	/**
	 * This method updates the database. It exists so that other classes do not have
	 * to directly make calls to the database, but instead this can be done here.
	 *
	 * @param query The SQL Query which will be called to the database
	 * @param args  The argument which can be passed into the SQL call
	 */
	public void updateData(String query, List<String> args) {
		if (this.isLocked) {
			System.out.println("Attempt to update database while closed");
			return;
		}
		try {
			PreparedStatement prep = conn.prepareStatement(query);
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					prep.setString(i + 1, args.get(i));
				}
			}
			prep.executeUpdate();
			prep.close();
		} catch (NullPointerException e) {
			System.out.println("ERROR: Database is not connected");
		} catch (SQLException e) {
			System.out.println("Error: SQL connection error");
			e.printStackTrace();
		}
	}

	/**
	 * This method reverts to a previous database if possible when reading in a bad
	 * filepath.
	 */
	public void readPrevDB() {
		if (prevFiles.size() > 2) {
			this.read(prevFiles.get(prevFiles.size() - 2));
		} else {
			System.out.println("Old database not stored, database could not be reverted to previous");
		}
	}

	/**
	 * Gets the user with the corresponding userID from the database.
	 * 
	 * @param userID The user ID to query the database with
	 * @return The corresponding User object with the given user ID,
	 * or null if there is no such user
	 * @throws SQLException if the user is not in the database
	 */
	public User getUser(String userID) throws SQLException {
		User user = null;
		List<List<String>> userStrings = new ArrayList<>();
		if (userID != null && !userID.equals("")) {
			userStrings = queryData("SELECT * FROM users WHERE userID = ? ;", Arrays.asList(userID));
		}
		if (userStrings.size() != 0) {
			List<String> foundUser = userStrings.get(0);
			if (foundUser.size() != 0) {
				user = new User(foundUser);
			} else {
				throw new SQLException("User is not in database or has no information");
			}
		}
		return user;
	}

	/**
	 * Gets the user with the corresponding username/password pair from the
	 * database.
	 * 
	 * @param username The username to query the database with
	 * @param password The password to query the database with
	 * @return The corresponding User object with the given username and password,
	 * or null if there is no such user
	 * @throws SQLException if the user is not in the database
	 */
	public User getUser(String username, String password) throws SQLException {
		User user = null;
		List<List<String>> qResults = new ArrayList<>();
		List<String> userStrings = new ArrayList<>();
		if ((username != null && !username.equals("")) && (password != null && !password.equals(""))) {
			qResults = queryData("SELECT * FROM users WHERE username = ? AND authentication = ?;",
					Arrays.asList(username, password));
		}
		if (qResults.size() > 0) {
			userStrings = qResults.get(0);
		} else {
			throw new SQLException("User is not in database or has no information");
		}
		//creates a User object with the information found in the database
		if (userStrings.size() > 3) {
			user = new User(userStrings);
		} else {
			throw new SQLException("User is not in database or has no information");
		}
		return user;

	}

	/**
	 * Method that adds a new user to the database.
	 * 
	 * @param userID,         the new user's id
	 * @param username,       the new user's username.
	 * @param reputation,     the user's initial reputation.
	 * @param email,          user's email.
	 * @param authentication, user's password.
	 * @throws SQLException
	 */
	public void addNewUser(String userID, String username, String reputation, String email, String authentication)
			throws SQLException {
		if (this.isLocked) {
			System.out.println("Attempt to update database while closed");
			return;
		}
		updateData("INSERT INTO Users (userID, username, reputation, email, authentication) VALUES (?, ?, ?, ?, ?)",
				Arrays.asList(userID, username, reputation, email, authentication));
	}

	/**
	 * Method that updates the reputation of a given user in the database.
	 * 
	 * @param userID, user ID of user to change rep.
	 * @param newRep, the new reputation of the user.
	 * @throws SQLException
	 */
	public void updateReputation(String userID, String newRep) throws SQLException {
		if (userID != null && !userID.equals("") && Integer.parseInt(newRep) >= 0) {
			updateData("UPDATE users SET reputation = ? WHERE userID = ? ;", Arrays.asList(newRep, userID));
		} else {
			throw new SQLException("User is not in database or has no information");
		}
	}

	/**
	 * Finds the champion with the given champ name.
	 * 
	 * @param champName the name of the champion to query the database
	 * @return the Main.Champion object with the given name
	 * @throws SQLException if the champion is not found
	 */
	public Champion getChampion(String champName) throws SQLException {

		Champion champ = null;
		List<String> champStringsW = new ArrayList<>();
		List<String> champStringsB = new ArrayList<>();
		List<String> champStringsP = new ArrayList<>();

		if (champName != null && !champName.equals("")) {
			champStringsW
					.addAll(queryData("SELECT * FROM WinRate WHERE champion = ? ;", Arrays.asList(champName)).get(0));
			champStringsB
					.addAll(queryData("SELECT * FROM BanRate WHERE champion = ? ;", Arrays.asList(champName)).get(0));
			champStringsP
					.addAll(queryData("SELECT * FROM PickRate WHERE champion = ? ;", Arrays.asList(champName)).get(0));

		}
		if (champStringsW.size() != 0 && champStringsB.size() != 0 && champStringsP.size() != 0) {
			champ = new Champion(champName, champStringsW, champStringsB, champStringsP);
		} else {
			throw new SQLException("Champion is not in database or has no information");
		}

		return champ;

	}

	public void addChampion(String champ) throws SQLException {
		if (this.isLocked) {
			System.out.println("Attempt to update database while closed");
			return;
		}
		updateData("INSERT INTO Winrate (champion) VALUES (?)", Arrays.asList(champ));
		updateData("INSERT INTO Pickrate (champion) VALUES (?)", Arrays.asList(champ));
		updateData("INSERT INTO Banrate (champion) VALUES (?)", Arrays.asList(champ));
	}

	/**
	 * Gets the win rate of a certain champion during a certain patch.
	 * 
	 * @param patchNum, the patch number to search.
	 * @param champ,    the champion's name to search.
	 * @return the win rate of champion.
	 * @throws SQLException
	 */
	public float getChampionWinRateFromPatch(String patchNum, String champ) throws SQLException {
		float winRate = 0;
		if (champ != null && !champ.equals("")) {
			String win = "SELECT %s FROM Winrate WHERE champion = ? ;";
			win = String.format(win, "\"patch" + patchNum + "\"");
			List<List<String>> r = queryData(win, Arrays.asList(champ));
			if (r.size() == 0) {
				System.out.println("No champion found");
				return winRate;
			} else if (r.get(0).get(0) == null){
				System.out.println("Champion had null data");
				return winRate;	
			}
			winRate = Float.parseFloat(r.get(0).get(0));
		} else {
			throw new SQLException("No relevant entry. Try running stat fetcher API.");
		}
		return winRate;

	}

	/**
	 * Gets the pick rate of a certain champion during a certain patch.
	 * 
	 * @param patchNum, the patch number to search.
	 * @param champ,    the champion's name to search.
	 * @return the pick rate of champion.
	 * @throws SQLException
	 */
	public float getChampionPickRateFromPatch(String patchNum, String champ) throws SQLException {
		float pickRate = 0;
		if (champ != null && !champ.equals("")) {
			String pick = "SELECT %s FROM Pickrate WHERE champion = ? ;";
			pick = String.format(pick, "\"patch" + patchNum + "\"");
			List<List<String>> r = queryData(pick, Arrays.asList(champ));
			if (r.size() == 0) {
				return pickRate;
			} else if (r.get(0).get(0) == null){
				System.out.println("Champion had null data");
				return pickRate;	
			}
			pickRate = Float.parseFloat(r.get(0).get(0));
		} else {
			throw new SQLException("No relevant entry. Try running stat fetcher API.");
		}
		return pickRate;

	}

	/**
	 * Gets the ban rate of a certain champion during a certain patch.
	 * 
	 * @param patchNum, the patch number to search.
	 * @param champ,    the champion's name to search.
	 * @return the ban rate of champion.
	 * @throws SQLException
	 */
	public float getChampionBanRateFromPatch(String patchNum, String champ) throws SQLException {
		float banRate = 0;
		if (champ != null && !champ.equals("")) {
			String ban = "SELECT %s FROM Banrate WHERE champion = ? ;";
			ban = String.format(ban, "\"patch" + patchNum + "\"");
			List<List<String>> r = queryData(ban, Arrays.asList(champ));
			if (r.size() == 0) {
				return banRate;
			} else if (r.get(0).get(0) == null){
				System.out.println("Champion had null data");
				return banRate;	
			}
			banRate = Float.parseFloat(r.get(0).get(0));
		} else {
			throw new SQLException("No relevant entry. Try running stat fetcher API.");
		}
		return banRate;

	}

	/**
	 * Adds a new patch to the win,pick, and ban rates tables.
	 * 
	 * @param patchNum, the new patch name.
	 * @throws SQLException
	 */
	public void createNewPatch(String patchNum) throws SQLException {
		String win = "ALTER TABLE WinRate ADD %s TEXT ;";
		win = String.format(win, "\"patch" + patchNum + "\"");
		String ban = "ALTER TABLE BanRate ADD %s TEXT ;";
		ban = String.format(ban, "\"patch" + patchNum + "\"");
		String pick = "ALTER TABLE PickRate ADD %s TEXT ;";
		pick = String.format(pick, "\"patch" + patchNum + "\"");
		updateData(win, null);
		updateData(ban, null);
		updateData(pick, null);
	}

	/**
	 * Sets the win, pick, and ban rates for a champion in a given patch
	 * 
	 * @param champ,    champion's name
	 * @param patchNum, the patch number to add the rates to.
	 * @param winRate,  new win rate.
	 * @param banRate,  new ban rate.
	 * @param pickRate, new pick rate.
	 * @throws SQLException
	 */
	public void addRatestoChamps(String champ, String patchNum, String winRate, String banRate, String pickRate)
			throws SQLException {
		String win = " UPDATE WinRate SET %s = ? WHERE champion = ? ;";
		win = String.format(win, "\"patch" + patchNum + "\"");
		String ban = " UPDATE BanRate SET %s = ? WHERE champion = ? ;";
		ban = String.format(ban, "\"patch" + patchNum + "\"");
		String pick = " UPDATE PickRate SET %s = ? WHERE champion = ? ;";
		pick = String.format(pick, "\"patch" + patchNum + "\"");
		updateData(win, Arrays.asList(winRate, champ));
		updateData(ban, Arrays.asList(banRate, champ));
		updateData(pick, Arrays.asList(pickRate, champ));
	}

	/**
	 * Gets the top 50 users by reputation.
	 * 
	 * @return the list of the
	 * @throws SQLException
	 */
	public List<User> getTopFifty() throws SQLException {
		List<User> topFifty = new ArrayList<User>();
		List<List<String>> topLists = queryData("SELECT * FROM users ORDER BY reputation + 0 DESC LIMIT 50", null);
		for (List<String> row : topLists) {
			topFifty.add(new User(row));
		}
		return topFifty;
	}

	/**
	 * Creates a new bet and inserts it into the database, then decrements the
	 * user's reputation
	 * 
	 * @param betID         The unique ID of the bet
	 * @param userID        The ID of the user who made the bet
	 * @param champion      The champion whose statistic is being bet on
	 * @param betType       The statistic that is being bet on
	 * @param betPercentage What the user bet the resulting change will be
	 * @param betAmount     The amount of reputation bet
	 * @param patch 		The patch the bet was made on
	 * @throws SQLException
	 * @throws RepException
	 */
	public void createNewBet(String betID, String userID, String champion, String betType, String betPercentage,
			String betAmount, String patch) throws SQLException, RepException {
		if (this.isLocked) {
			System.out.println("Attempt to update database while closed");
			return;
		}
		if (getUser(userID).getReputation() - Integer.parseInt(betAmount) < 0) {
			throw new RepException("User does not have enough reputation to place that bet");
		}
		updateData(
				"INSERT INTO Bets (betID, userID, champion, betType, betPercentage, betAmount, patch, gain) VALUES (?, ?, ?, ?, ?, ?, ?, 0) ;",
				Arrays.asList(betID, userID, champion, betType, betPercentage, betAmount, patch));
		updateReputation(userID, String.valueOf(getUser(userID).getReputation() - Integer.parseInt(betAmount)));

	}

	/**
	 * Adds reputation to a user in the database.
	 * 
	 * @param reputationChange - reputation to add
	 * @param userID           - user to add reputation to
	 */
	public void addToUserReputation(Integer reputationChange, String userID) {
		int userRep;
		try {
			userRep = getUser(userID).getReputation();
			if (userRep + reputationChange < 0) {
				reputationChange = userRep;
			}
			updateData("UPDATE Users SET Reputation = Reputation + ? WHERE userID = ?", Arrays.asList(String.valueOf(reputationChange), userID));
		} catch (SQLException e) {
			e.printStackTrace();
		}
  }

  public class RepException extends Exception {

		/**
		 * Exception for invalid user reputation changes
		 */
		private static final long serialVersionUID = 1L;
		public RepException(String error){
			super(error);
		}
		
	}

	/**
	 * Method to get a bet from the database
	 * 
	 * @param betID the ID of the bet to find
	 * @return the Bet object with the given bet ID
	 * @throws SQLException
	 */
	public Bet getBet(String betID) throws SQLException {
		Bet bet = null;
		//gets the information of the bet stored in the database
		List<String> betStrings = new ArrayList<>();
		if (betID != null && !betID.equals("")) {
			betStrings = queryData("SELECT * FROM Bets WHERE betID = ? ;", Arrays.asList(betID)).get(0);
		}
		//creates a Bet object from the information
		if (betStrings.size() != 0) {
			SigmoidAdjustedGain gainFunc = new SigmoidAdjustedGain(1.5, 0.75, 0.0, 0.0);
			bet = new Bet(gainFunc, betStrings);
		} else {
			throw new SQLException("Bet is not in database or has no information");
		}
		return bet;

	}

		/**
	 * Method to get the list of bets by a user on a certain patch
	 * 
	 * @param userID the ID of the bet to find
	 * @return the Bet object with the given bet ID
	 * @throws SQLException
	 */
	public List<Bet> getUserBetsOnPatch(String patch, String userID) throws SQLException {
		Bet bet = null;
		List<Bet> bets = new ArrayList();
		List<List<String>> betStrings = new ArrayList<>();
		if (patch != null && !patch.equals("")) {
			betStrings = queryData("SELECT * FROM Bets WHERE patch = ? and userID = ?;", Arrays.asList(patch, userID));
		}
		if (betStrings.size() != 0) {
			SigmoidAdjustedGain gainFunc = new SigmoidAdjustedGain(1.5, 0.75, 0.0, 0.0);
			//makes a new bet object from each bet in the database
			for (List<String> string : betStrings){
				if(string.size() != 0){
					bets.add(new Bet(gainFunc, string));
				}
			}
		}
		return bets;

	}

	/**
	 * Method that counts the number of bets for a given champion.
	 * 
	 * @param champ the champion's name
	 * @param patch The patch for the bet
	 * @return number of bets submitted for that
	 *               champion
	 * @throws SQLException
	 */
	public int countNumberOfBets(String champ, String patch) throws SQLException {
		int numBets = 0;
		numBets = Integer.parseInt(
				queryData("SELECT COUNT(champion) FROM Bets WHERE champion = ? AND patch = ? ; ", Arrays.asList(champ, patch)).get(0).get(0));
		return numBets;
	}

	/**

	 * Gets a list of patches from the database.
	 * @return List of patches
	 * @throws SQLException
	 */
	public List<List<String>> getPatches() throws SQLException {
		return queryData(
				"SELECT col_name from (SELECT m.name AS table_name, p.cid AS col_id, p.name AS col_name, p.type AS col_type, p.pk AS col_is_pk, p.dflt_value AS col_default_val,p.[notnull] AS col_is_not_null FROM sqlite_master m LEFT OUTER JOIN pragma_table_info((m.name)) p ON m.name <> p.name WHERE m.type = 'table' ORDER BY table_name, col_id) WHERE table_name=\"BanRate\" AND col_name != \"champion\";",
				Arrays.asList());
	}

	/**
	 * Updates how much reputation each bet gained
	 * @param bet - bets to update gains for
	 */
	public void updateBetGains(Bet bet) {
		updateData("UPDATE Bets SET Gain = ? WHERE BetID = ?;", Arrays.asList(String.valueOf(bet.getGain()), bet.getBetID()));
	}

	/**
	 * Method that deletes all rows from a table.
	 * @param table, table to delete rows from
	 * @throws SQLException
	 */
	public void deleteData(String table) throws SQLException {
		String delete = "DELETE FROM %s";
		delete = String.format(delete, table);
		updateData(delete, null);
	}

}
