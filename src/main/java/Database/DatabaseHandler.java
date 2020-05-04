package Database;

import Betting.Bet;
import Betting.SigmoidAdjustedGain;
import Main.Champion;
import Main.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHandler {

	private static Connection conn;
	private final List<String> prevFiles;

	/**
	 * The constructor for the SqlDatabaseReader instantiates the list of previous
	 * files to be kept track of in case a bad file is read in.
	 */
	public DatabaseHandler() {
		prevFiles = new ArrayList<String>();
	}

	/**
	 * This method reads in a new database.
	 *
	 * @param filename The database file to be read in
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
			/*
			 * Statement stat = conn.createStatement();
			 * stat.executeUpdate("PRAGMA foreign_keys=ON;");
			 * System.out.println("Connected to " + filename); prevFiles.add(filename);
			 * stat.close();
			 */
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
	public static void updateData(String query, List<String> args) {
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
	 * @return The corresponding User object with the given user ID
	 * @throws SQLException if the user is not in the database
	 */
	public User getUser(String userID) throws SQLException {
		User user = null;
		List<String> userStrings = new ArrayList<>();
		if (userID != null && !userID.equals("")) {
			userStrings = queryData("SELECT * FROM users WHERE userID = ? ;", Arrays.asList(userID)).get(0);
		}
		if (userStrings.size() != 0) {
			user = new User(userStrings);
		} else {
			throw new SQLException("User is not in database or has no information");
		}
		return user;

	}

	/**
	 * Gets the user with the corresponding username/password pair from the
	 * database.
	 * 
	 * @param username The username to query the database with
	 * @param password The password to query the database with
	 * @return The corresponding User object with the given username and
	 *         password
	 * @throws SQLException if the user is not in the database
	 */
	public User getUser(String username, String password) throws SQLException {
		User user = null;
		System.out.println("F");
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

		System.out.println("User Strings for current user: " + userStrings.toString());
		if (userStrings.size() > 3) {
			user = new User(userStrings);
			System.out.println("Successfully got user with username " + username);
		} else {
			throw new SQLException("User is not in database or has no information");
		}
		return user;

	}

	/**
	 * Method that adds a new user to the database.
	 * @param userID, the new user's id
	 * @param username, the new user's username.
	 * @param reputation, the user's initial reputation.
	 * @param email, user's email.
	 * @param authentication, user's password.
	 * @throws SQLException
	 */
	public void addNewUser(String userID, String username, String reputation, String email, String authentication)
			throws SQLException {
		updateData("INSERT INTO Users (userID, username, reputation, email, authentication) VALUES (?, ?, ?, ?, ?)",
				Arrays.asList(userID, username, reputation, email, authentication));
	}
	
	/**
	 * Method that updates the reputation of a given user.
	 * @param userID, user ID of user to change rep.
	 * @param newRep, the new reputation of the user.
	 * @throws SQLException
	 */
	public void updateReputation(String userID, String newRep) throws SQLException {
		if (userID != null && !userID.equals("") && Integer.parseInt(newRep) > 0) {
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
			champ = new Champion(champStringsW, champStringsB, champStringsP);
		} else {
			throw new SQLException("Champion is not in database or has no information");
		}

		return champ;

	}

	// have a champion table with rates, dont need rep query if its a field of user

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
			winRate = Float.parseFloat(queryData("SELECT ? FROM WinRate WHERE champion = ? ;",
					Arrays.asList("Patch" + patchNum, champ)).get(0).get(0));
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
			pickRate = Float.parseFloat(queryData("SELECT ? FROM PickRate WHERE champion = ? ;",
					Arrays.asList("Patch" + patchNum, champ)).get(0).get(0));
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
			banRate = Float.parseFloat(queryData("SELECT ? FROM BanRate WHERE champion = ? ;",
					Arrays.asList("Patch" + patchNum, champ)).get(0).get(0));
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
		updateData("ALTER TABLE WinRate ADD ? NUMERIC", Arrays.asList("Patch" + patchNum));
		updateData("ALTER TABLE BanRate ADD ? NUMERIC", Arrays.asList("Patch" + patchNum));
		updateData("ALTER TABLE PickRate ADD ? NUMERIC", Arrays.asList("Patch" + patchNum));
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
		updateData(" UPDATE WinRate SET ? = ? WHERE champion = ? ;",
				Arrays.asList("Patch" + patchNum, winRate, champ));
		updateData(" UPDATE BanRate SET ? = ? WHERE champion = ? ;",
				Arrays.asList("Patch" + patchNum, banRate, champ));
		updateData(" UPDATE PickRate SET ? = ? WHERE champion = ? ;",
				Arrays.asList("Main.Patch" + patchNum, pickRate, champ));
	}

	/**
	 * Gets the top 50 users by reputation.
	 * 
	 * @return the list of the
	 * @throws SQLException
	 */
	public List<User> getTopFifty() throws SQLException {
		List<User> topFifty = new ArrayList<User>();
		for (List<String> row : queryData("SELECT * FROM users ORDER BY reputation DESC LIMIT 50", null)) {
			topFifty.add(new User(row));
		}
		return topFifty;
	}

	/**
	 * Creates a new bet and inserts it into the database, then decrements the user's reputation
	 * 
	 * @param betID The unique ID of the bet
	 * @param userID The ID of the user who made the bet
	 * @param champion The champion whose statistic is being bet on
	 * @param betType The statistic that is being bet on
	 * @param betPercentage What the user bet the resulting change will be
	 * @param betAmount The amount of reputation bet
	 * @throws SQLException
	 */
	public void createNewBet(String betID, String userID, String champion, String betType, String betPercentage,
			String betAmount) throws SQLException {
		System.out.println("Bet made with id " + betID);
		updateData(
				"INSERT INTO Bets (betID, userID, champion, betType, betPercentage, betAmount) VALUES (?, ?, ?, ?, ?, ?)",
				Arrays.asList(betID, userID, champion, betType, betPercentage, betAmount));
		updateReputation(userID, String.valueOf(getUser(userID).getReputation() - Integer.parseInt(betAmount)));

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
		List<String> betStrings = new ArrayList<>();
		if (betID != null && !betID.equals("")) {
			betStrings = queryData("SELECT * FROM Bets WHERE betID = ? ;", Arrays.asList(betID)).get(0);
		}
		if (betStrings.size() != 0) {
			SigmoidAdjustedGain gainFunc = new SigmoidAdjustedGain(1.5, 0.75, 0.0, 0.0);
			bet = new Bet(gainFunc, betStrings);
		} else {
			throw new SQLException("Bet is not in database or has no information");
		}
		return bet;

	}

	/**
	 * Method that counts the number of bets for a given champion.
	 * 
	 * @param champ, the champion's name @return, number of bets submitted for that
	 *               champion
	 * @throws SQLException
	 */
	public int countNumberOfBets(String champ) throws SQLException {
		int numBets = 0;
		numBets = Integer.parseInt(
				queryData("SELECT COUNT(champion) FROM Bets WHERE champion = ?; ", Arrays.asList(champ)).get(0).get(0));
		return numBets;
	}

	public List<List<String>> getPatches() throws SQLException {
		return queryData(
				"SELECT col_name from (SELECT m.name AS table_name, p.cid AS col_id, p.name AS col_name, p.type AS col_type, p.pk AS col_is_pk, p.dflt_value AS col_default_val,p.[notnull] AS col_is_not_nullFROM sqlite_master mLEFT OUTER JOIN pragma_table_info((m.name)) p ON m.name <> p.name WHERE m.type = 'table' ORDER BY table_name, col_id) WHERE table_name=\"BanRate\" AND col_name != \"champion\"",
				Arrays.asList());
	}

}
