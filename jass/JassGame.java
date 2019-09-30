package javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javass.jass.Card.Color;
import javass.jass.Card.Rank;

/**
 * The representation of a game of Jass.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 * 
 */
public final class JassGame {

    private static final Card SEVEN_OF_DIAMONDS = Card.of(Color.DIAMOND, Rank.SEVEN);
    
	private final Map<PlayerId, Player> players;
	private final Map<PlayerId, String> playerNames;
	private final Random shuffleRng;
	private final Random trumpRng;

	private final Map<Player, CardSet> hands = new HashMap<>();
	private final List<Card> deck = new ArrayList<>();
	private PlayerId firstPlayerId;
	private Color trump;

	private TurnState currentTurnState;

	private boolean isGameOver = false;

	/**
	 * The representation of a game of Jass.
	 * @param rngSeed the seed for the random number generator (for the shuffle and the trump).
	 * @param players a map between the {@link PlayerId}s and the {@link Player}s of the game. 
	 * @param playerNames a map between the {@link Players}s of the game and their names. 
	 */
	public JassGame(long rngSeed, Map<PlayerId, Player> players,
			Map<PlayerId, String> playerNames) {
		this.players = Collections.unmodifiableMap(new EnumMap<>(players));
		this.playerNames = Collections
				.unmodifiableMap(new EnumMap<>(playerNames));

		Random rng = new Random(rngSeed);
		this.shuffleRng = new Random(rng.nextLong());
		this.trumpRng = new Random(rng.nextLong());
	}

	/**
	 * A method to check if the game is over (if a team has more than 1000).
	 * @return a boolean which is true if a team has won
	 */
	public boolean isGameOver() {
		return isGameOver;
	}

	/**
	 * A method that plays the game until the end of the current {@link Trick}.
	 */
	public void advanceToEndOfNextTrick() {
	    if(!isGameOver) {
    	    //Creates the first turn state if it does not exist yet.
    		if (currentTurnState == null)
    			newTurn(true);
    
    		//Collects the trick when it is full.
    		if (currentTurnState.trick().isFull())
    			currentTurnState = currentTurnState.withTrickCollected();
    
    		//Creates the next turn if all the tricks of the current turn have been played.
    		if (currentTurnState.isTerminal())
    			newTurn(false);
    
    		//Calls the methods to update the score for all players.
    		updateScoreForAll();
            //Calls the methods to update the trick for all players.
    		updateTrickForAll();
    
    		//While the trick isn't full ask each player which card they play and update the trick.
    		while (!currentTurnState.trick().isFull())
    			updatePlayers();
    
    		checkIfTeamWon();
	    }
	}

	private void newTurn(boolean isFirst) {
		trump = Color.ALL.get(trumpRng.nextInt(Color.COUNT));
		initializeAndShuffleDeck();
		initializePlayers(isFirst);
		
		if (!isFirst) {
			setNewFirstPlayer();
			currentTurnState = TurnState.initial(trump,
					currentTurnState.score().nextTurn(), firstPlayerId);
		} else
			currentTurnState = TurnState.initial(trump, Score.INITIAL,
					firstPlayerId);
	}

	private void checkIfTeamWon() {
	    for (int i = 0; i < TeamId.COUNT; i++) {
	        if (currentTurnState
	                .score()
	                .totalPoints(TeamId.ALL.get(i)) >= Jass.WINNING_POINTS)
	            updateWinningTeam(TeamId.ALL.get(i));
        }
	}

	private void initializeAndShuffleDeck() {
		deck.clear();

		for (Color color : Color.ALL)
		    for (Rank rank : Rank.ALL) 
                deck.add(Card.of(color, rank));
		
		Collections.shuffle(deck, shuffleRng);
	}

	private void initializePlayers(boolean first) {
		for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
			Player p = entry.getValue();
			PlayerId iD = entry.getKey();

			if (first)
				p.setPlayers(entry.getKey(), playerNames);

			//Distributes the cards among players.
			hands.put(p, CardSet.of(deck.subList(iD.ordinal() * Jass.HAND_SIZE,
					(iD.ordinal() + 1) * Jass.HAND_SIZE)));
			p.updateHand(hands.get(p));
			
			p.setTrump(trump);

			//Compute which player is the first player i.e has the jack of diamonds.
			if (first && hands.get(p)
					.contains(SEVEN_OF_DIAMONDS))
				firstPlayerId = iD;
		}
	}

	private void updatePlayers() {
		PlayerId pid = currentTurnState.nextPlayer();
		Player p = players.get(pid);

		//Asks the player which card he wants to play.
		Card c = p.cardToPlay(currentTurnState, hands.get(p));

		//Updates the turn with the card played
		currentTurnState = currentTurnState.withNewCardPlayed(c);

		//Updates the hand of the player with the card played.
		hands.put(p, hands.get(p).remove(c));
		p.updateHand(hands.get(p));

		//Calls the methods to update the trick for all players.
		updateTrickForAll();
	}

	private void updateTrickForAll() {
		for (Player p : players.values())
			p.updateTrick(currentTurnState.trick());
	}

	private void updateScoreForAll() {
		for (Player p : players.values())
			p.updateScore(currentTurnState.score());
	}

	private void setNewFirstPlayer() {
		firstPlayerId = PlayerId.ALL
				.get((firstPlayerId.ordinal() + 1) % PlayerId.COUNT);
	}

	private void updateWinningTeam(TeamId team) {
        isGameOver = true;
		for (Player p : players.values()) {
			p.updateScore(currentTurnState.score());
			p.setWinningTeam(team);
		}
	}
}
