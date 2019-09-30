package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

import static ch.epfl.javass.Preconditions.*;

/**
 * The representation of the state of a turn during a game of Jass.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class TurnState {
    
    /**
     * Creates the initial state of a turn 
     * @param trump the trump color of the turn
     * @param score the score at the beginning of the turn
     * @param firstPlayer the first player of the turn
     * @return the initial state of the turn
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        long aS = score.packed();
        long uC = PackedCardSet.ALL_CARDS;
        int aT = PackedTrick.firstEmpty(trump, firstPlayer);
        return new TurnState(aS, uC, aT);
    }
    
    /**
     * Creates a new turnstate given a packed score, a packed set of unplayed cards and a packed trick.
     * @param pkScore the packed version of the score the turn will have.
     * @param pkUnplayedCards the packed version of the set of cards that have not yet been played during the turn.
     * @param pkTrick the packed version of the trick the turn will have
     * @throws IllegalArgumentException if pkScore, pkUnplayedCards and pkTrick are not valid.
     * @return the new turn state with the new score, set of unplayed cards and trick.
     */
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        checkArgument(PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick));
                
        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    private final long currentScore;
    private final long unplayedCards;
    private final int currentTrick;
    
    private TurnState(long aS, long uC, int aT){        
        currentScore = aS;
        unplayedCards = uC;
        currentTrick = aT;
    }
    
    /**
     * Gets the packed version of the current score of the turn.
     * @return a long the packed version of the score of the turn.
     */
    public long packedScore() {
        return currentScore;
    }
    
    /**
     * Gets the packed version of the current unplayed cards of the turn.
     * @return a long the packed version of the set of unplayed cards of the turn.
     */
    public long packedUnplayedCards() {
        return unplayedCards;
    }
    
    /**
     * Gets the packed version of the current trick of the turn.
     * @return a long the packed version of the trick of the turn.
     */
    public int packedTrick() {
        return currentTrick;
    }
    
    /**
     * Gets the current score of the turn.
     * @return the score of the turn.
     */
    public Score score() {
        return Score.ofPacked(currentScore);
    }
    
    /**
     * Gets the current CardSet of unplayed cards of the turn.
     * @return the CardSet of unplayed cards of the turn.
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }
    
    /**
     * Gets the current trick of the turn.
     * @return the trick of the turn.
     */
    public Trick trick() {
        return Trick.ofPacked(currentTrick);
    }
    
    /**
     * Checks if the trick is the last one before the end of the turn.
     * @return a boolean if the trick is the last one.
     */
    public boolean isTerminal() {
        return currentTrick == PackedTrick.INVALID;
    }
    
    /**
     * Get the next player that will play in the turn.
     * @throws IllegalStateException if the current trick is full.
     * @return the PlayerId of the next player of the trick. 
     */
    public PlayerId nextPlayer() {
        if(trick().isFull())
            throw new IllegalStateException();
        
        return PackedTrick.player(currentTrick, PackedTrick.size(currentTrick));
    }
    
    /**
     * Compute the new turnState when a given card has been played
     * @param card the card to be played
     * @throws IllegalStateException if the trick is full.
     * @return the new turn state with the card played
     */
    public TurnState withNewCardPlayed(Card card) {
        if(trick().isFull() && unplayedCards().contains(card))
            throw new IllegalStateException();
        
        return ofPackedComponents(currentScore, PackedCardSet.remove(unplayedCards, card.packed()), PackedTrick.withAddedCard(currentTrick, card.packed()));
    }
    
    /**
     * Computes a new turn state after the trick has been collected, with updated score and a new trick.
     * @throws IllegalStateException the trick is not full.
     * @return the new turn state with the next trick and the updated score
     */
    public TurnState withTrickCollected() {
        if(!trick().isFull())
            throw new IllegalStateException();
        
        return new TurnState(
                PackedScore.withAdditionalTrick(currentScore, PackedTrick.winningPlayer(currentTrick).team(), PackedTrick.points(currentTrick)),
                unplayedCards,
                PackedTrick.nextEmpty(currentTrick));
    }
    
    /**
     * Computes a new turn state with a new card and collects the trick if it is full
     * @param card the card to be played
     * @return the new turn state when the given card is played or the trick is full 
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        TurnState t = withNewCardPlayed(card);
        
        if(t.trick().isFull())
                t = t.withTrickCollected();
        
        return t;
    }
}
