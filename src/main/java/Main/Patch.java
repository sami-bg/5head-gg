package Main;

import Betting.Bet;
import Betting.BettingSession;
import RiotAPI.ChampConsts;

import java.util.ArrayList;
import java.util.List;

public class Patch {
	private final String id;
	private final List<String> users;
	private final BettingSession winBets = new BettingSession("winRate", ChampConsts.getChampNames());
	private final BettingSession pickBets = new BettingSession("pickRate", ChampConsts.getChampNames());
	private final BettingSession banBets = new BettingSession("banRate", ChampConsts.getChampNames());

	/**
	 * Patch constructor.
	 * @param id The ID of the patch.
	 * @param users The list of users in the patch.
	 */
	public Patch(String id, List<String> users) {
		this.id =id;
		this.users = users;
	}
	
	public List<String> getUsers() {
		return users;
	}

	/**
	 * winBets getter.
	 * @return list of bets that bet on the win rate in this session.
	 */
	public BettingSession getWinBets() {
		return winBets;
	}

	/**
	 * pickBets getter.
	 * @return list of bets that bet on the pick rate in this session.
	 */
	public BettingSession getPickBets() {
		return pickBets;
	}

	/**
	 * banBets getter.
	 * @return list of bets that bet on the ban rate in this session.
	 */
	public BettingSession getBanBets() {
		return banBets;
	}

	/**
	 *
	 * @param id The ID of the user that made the bets
	 * @return - List of bets
	 */
	public List<Bet> getBets(String id) {
		List<Bet> userBets = new ArrayList<>();
		List<Bet> banBets = this.getBanBets().getBetsFromUserID(id);
		if (banBets != null) {
			userBets.addAll(banBets);
		}
		List<Bet> winBets = this.getWinBets().getBetsFromUserID(id);
		if (winBets != null) {
			userBets.addAll(winBets);
		}
		List<Bet> pickBets = this.getPickBets().getBetsFromUserID(id);
		if (pickBets != null) {
			userBets.addAll(pickBets);
		}
		return userBets;
	}
}
