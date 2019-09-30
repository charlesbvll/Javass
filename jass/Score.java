package ch.epfl.javass.jass;

import static ch.epfl.javass.Preconditions.*;

/**
 * 
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class Score {
    
    public static final Score INITIAL = new Score(PackedScore.INITIAL);
    
    /**
     * Check if the packed score is valid.
     * @param a long packed the packed score of the team 
     * @throws IllegalArgumentException if packed is not valid.
     * @return the score associated with packed.
     */
    public static Score ofPacked(long packed) {
        checkArgument(PackedScore.isValid(packed));
        Score s = new Score(packed);
        return s;
    }
    
    private final long packed;
    
    private Score(long packed) {
        this.packed = packed;
    }
    
    /**
     * Gives the packed version of the Score.
     * @return a long representing the packed version of the score.
     */
    public long packed() {
        return packed;
    }
    
    /**
     * Gives the number of tricks made by a given team in the current turn.
     * @param t the teamId for the wanted number of tricks.
     * @return an int the number of tricks gained by the team during the receiver's current turn.
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(packed, t);
    }
    
    /**
     * Gives the number of points made by a given team in the current turn.
     * @param t the team.
     * @return an int the number of points gained by the team during the receiver's current turn.
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(packed, t);
    }
    
    /**
     * Gives the number of points made by a given team before the current turn.
     * @param t the team.
     * @return an int the number of points gained by the team during the receiver's previous turns (excluding the current one).
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(packed, t);
    }
    
    /**
     * Gives the number of points made by a given team in the current turn and before.
     * @param t the team.
     * @return an int the total number of points gained by the team t during the receiver's current game.
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(packed, t);
    }
    
    /**
     * Computes the {@link Score} of the team after a {@link Trick} has been played.
     * @param winningTeam the teamId of the team that won the trick.
     * @param trickPoints number of tricks gained by the team during the current turn.
     * @throws IllegalArgumentException if trickPoints is negative.
     * @return the updated scores.
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        checkArgument(trickPoints>=0);
        
        return new Score(PackedScore.withAdditionalTrick(packed, winningTeam, trickPoints));
    }
    
    /**
     * Gives the {@link Score} at the beginning of the next turn.
     * @return the updated scores for the next turn
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(packed));
    }
    
    @Override
    public boolean equals(Object that0) {
        return that0 != null 
                && that0.getClass() == this.getClass() 
                && this.packed() == ((Score)that0).packed();
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(packed);
    }
    
    @Override
    public String toString() {
        return PackedScore.toString(packed); 
    }
}
