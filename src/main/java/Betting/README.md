Betting module!


Bet(Double initialValue, loss function, String id)

BettingSession - Wrapper for different kinds of bets: each Session corresponds to i.e. winrate, pickrate, banrate.
Contains a notion of a user and their corresponding bets.

BettingSession.broadcast(result, champion);

Bet(2% expectedIncrease, category, user, 0-1loss) <-- 51% -> -50
52 <-- 45 --> -1000

lambda x, y --> myLoss(x: predict, y: result)

Main.User makes winrate bet --> BettingSessionWinrate.addBet(betObj)
    

List of Bets
Map of Main.User ID to List of Bets

