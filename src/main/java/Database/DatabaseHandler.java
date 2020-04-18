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
			userStrings = this.queryData("SELECT * FROM users WHERE id = ?", Arrays.asList(userID));
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
			userStrings = this.queryData("SELECT * FROM users WHERE id = ?", Arrays.asList(champName));
		}
		if (userStrings.size() != 0){
			champ = new Champion(userStrings);
		} else {
			throw new SQLException("User is not in database or has no information");
		}
        return champ;

    }

}
