package Database;

import Main.Main;
import RiotAPI.ChampConsts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Database.DatabaseHandler.RepException;

/**
 * Class that populates the database for testing purposes.
 *
 */
public class DatabaseEntryFiller {

	// DatabaseHandler db = new DatabaseHandler();

	List<Integer> userIDs = new ArrayList<Integer>();

	// Im thinking we should have all elements in db as strings for simplicity.
	/**
	 * Adds users to database.
	 * 
	 * @param numberOfUsers, number of users to add.
	 * @throws SQLException
	 */
	public void addUsers(int numberOfUsers) throws SQLException {

		for (int i = 0; i < numberOfUsers; i++) {
			userIDs.add(i);
			Random rand = new Random();

			// Generate random integers in range 0 to 999
			int rand_int = rand.nextInt(1000);

			Main.db.addNewUser(String.valueOf(i), "user" + i, String.valueOf(rand_int), "Email" + i, "user" + i);
		}
	}

	/**
	 * Adds bets to database.
	 * 
	 * @param numberOfBets, number of bets to add.
	 * @throws SQLException
	 */
	public void addBets(int numberOfBets, String patch) throws SQLException {

		for (int i = 0; i < numberOfBets; i++) {
			Random rand = new Random();

			List<String> betTypes = new ArrayList<String>();
			betTypes.add("Pick");
			betTypes.add("Ban");
			betTypes.add("Win");

			String betType = betTypes.get(rand.nextInt(betTypes.size()));

			String champ = ChampConsts.getChampNames().get(rand.nextInt(ChampConsts.getChampNames().size()));

			int rand_index = rand.nextInt(userIDs.size());
			String userID = String.valueOf(userIDs.get(rand_index));
			userIDs.remove(rand_index);

			try {
				Main.db.createNewBet("bet " + i, userID, champ, betType, "0.5", String.valueOf(rand_index), patch);
			} catch (RepException e) {
				// TODO Auto-generated catch block
				
			}
		}
	}

	public static void addChampsToRatesTables(DatabaseHandler db){
		for (String champ : ChampConsts.getChampNames()){
			try {
				db.addChampion(champ);
			} catch (SQLException e) {
				System.out.println("Table already initialized with " + champ);
			}
		}
	}

}
