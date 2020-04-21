package main.java.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.Champion;
import main.java.User;

public class DatabaseHandler {

	private static Connection conn;
	private List<String> prevFiles;

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
			Statement stat = conn.createStatement();
			stat.executeUpdate("PRAGMA foreign_keys=ON;");
			System.out.println("Connected to " + filename);
			prevFiles.add(filename);
			stat.close();
		} catch (SQLException e) {
			System.out.println("SQLException when creating connection to database -- Database doesn't exist");
		}
	}

	/**
	 * This method queries the database. It exists so that other classes do not have
	 * to directly make calls to the database, but instead this can be done here.
	 *
	 * @param query The SQL Query which will be called to the database
	 * @param arg   The argument which can be passed into the SQL call
	 * @return A list containing the elements from the sql call
	 */
	public List<String> queryData(String query, List<String> args) {
		List<String> res;
		try {
			PreparedStatement prep = conn.prepareStatement(query);
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					prep.setString(i, args.get(i));
				}
			}
			ResultSet result = prep.executeQuery();

			res = new ArrayList<String>();
			while (result.next()) {
				res.add(result.getString(1));
			}
			prep.close();
			result.close();
		} catch (NullPointerException e) {
			System.out.println("ERROR: Database is not connected");
			return new ArrayList<String>();
		} catch (SQLException e) {
			System.out.println("Error: SQL connection error");
			return new ArrayList<String>();
		}
		return res;
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

	public User getUser(String userID) throws SQLException {
		User user = null;
		List<String> userStrings = new ArrayList<>();
		if (userID != null && !userID.equals("")){
			userStrings = this.queryData("SELECT * FROM users WHERE id = ? ;", Arrays.asList(userID));
		}
		if (userStrings.size() != 0){
			user = new User(userStrings);
		} else {
			throw new SQLException("User is not in database or has no information");
		}
        return user;

	}
	
	public Champion getChampion(String champName) throws SQLException {
		Champion champ = null;
		List<String> userStrings = new ArrayList<>();
		if (champName != null && !champName.equals("")){
			userStrings = this.queryData("SELECT * FROM users WHERE id = ? ;", Arrays.asList(champName));
		}
		if (userStrings.size() != 0){
			champ = new Champion(userStrings);
		} else {
			throw new SQLException("User is not in database or has no information");
		}
        return champ;

    }
	
	//have a champion table with rates, dont need rep query if its a field of user
	
	public float getChampionWinRateFromPatch(String patchNum, String champ) throws SQLException {
		float winRate = 0;
		if (champ != null && !champ.equals("")){
			winRate = Float.parseFloat(this.queryData("SELECT ? FROM WinRate WHERE = ? ;", Arrays.asList("Patch" + patchNum, champ)).get(0));
		} else {
			throw new SQLException("No relevant entry. Try running stat fetcher API.");
		}
        return winRate;

	}
	
	public float getChampionpickRateFromPatch(String patchNum, String champ) throws SQLException {
		float pickRate = 0;
		if (champ != null && !champ.equals("")){
			pickRate = Float.parseFloat(this.queryData("SELECT ? FROM PickRate WHERE = ? ;", Arrays.asList("Patch" + patchNum, champ)).get(0));
		} else {
			throw new SQLException("No relevant entry. Try running stat fetcher API.");
		}
        return pickRate;

	}
	
	public float getChampionBanRateFromPatch(String patchNum, String champ) throws SQLException {
		float banRate = 0;
		if (champ != null && !champ.equals("")){
			banRate = Float.parseFloat(this.queryData("SELECT ? FROM BanRate WHERE = ? ;", Arrays.asList("Patch" + patchNum, champ)).get(0));
		} else {
			throw new SQLException("No relevant entry. Try running stat fetcher API.");
		}
        return banRate;

	}
	
	public void createNewPatch(String patchNum) throws SQLException {
		this.queryData("ALTER TABLE WinRate ADD ? NUMERIC", Arrays.asList("Patch" + patchNum));
		this.queryData("ALTER TABLE BanRate ADD ? NUMERIC", Arrays.asList("Patch" + patchNum));
		this.queryData("ALTER TABLE PickRate ADD ? NUMERIC", Arrays.asList("Patch" + patchNum));
	}
	
	public void addRatestoChamps(String champ, String patchNum, String winRate, String banRate, String pickRate) throws SQLException {
		
		this.queryData(" UPDATE WinRate SET ? = ? WHERE champion = ? ;", Arrays.asList("Patch" + patchNum, winRate, champ));
		this.queryData(" UPDATE BanRate SET ? = ? WHERE champion = ? ;", Arrays.asList("Patch" + patchNum, banRate, champ));
		this.queryData(" UPDATE PickRate SET ? = ? WHERE champion = ? ;", Arrays.asList("Patch" + patchNum, pickRate, champ));

	}
	
	

}
