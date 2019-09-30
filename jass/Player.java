package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * An interface that represent a player of a game of Jass.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public interface Player {
    
	/**
	 * A method to choose which card to play
	 * @param state the current TurnState of the game
	 * @param hand the current hand of the Player
	 * @return the card to be played
	 */
    abstract Card cardToPlay(TurnState state, CardSet hand);
	
	/**
	 * Associates the Players with their name
	 * @param ownId the iD of the Player
	 * @param playerNames a map that associates the PlayerId with the names of the players
	 */
	default void setPlayers(PlayerId ownId, Map<PlayerId, String>playerNames) {
	}
	
	/**
	 * Updates the hand of the player with the new current hand
	 * @param newHand
	 */
	default void updateHand(CardSet newHand) {
	}
	
	/**
	 * Updates the trump color of the player with the new current trump
	 * @param trump
	 */
	default void setTrump(Color trump) {
	}
	
	/**
	 * Updates the trick of the player with the new current trick
	 * @param newTrick
	 */
	default void updateTrick(Trick newTrick) {
	}
	
	/**
	 * Updates the score of the player with the new current score
	 * @param score
	 */
	default void updateScore(Score score) {
	}
	
	/**
	 * Sets the winning of the game
	 * @param winningTeam the team that won the game
	 */
	default void setWinningTeam(TeamId winningTeam) {
	}
	
	
}
