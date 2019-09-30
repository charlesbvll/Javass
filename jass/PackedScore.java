package ch.epfl.javass.jass;

import static ch.epfl.javass.Preconditions.checkArgument;
import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * The representation of a score of the game of Jass in a packed form (as a long).
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class PackedScore {
    private PackedScore() {}
    
    /**
     * A long representing the initial score.
     */
    public static final long INITIAL = 0b000000; 
    
    /**
     * Packs the six components of a {@link Score} in one long.
     * @param turnTricks1 number of tricks gained by team1 during the current turn.
     * @param turnPoints1 number of points gained by team1 during the current turn.
     * @param gamePoints1 number of points gained by team1 during the previous turns (excluding the current turn).
     * @param turnTricks2 number of tricks gained by team2 during the current turn.   
     * @param turnPoints2 number of points gained by team2 during the current turn.
     * @param gamePoints2 number of points gained by team2 during the previous turns (excluding the current turn).
     * @throws IllegalArgumentException if turnTricks1,turnPoints1,gamePoints1 && turnTricks2,turnPoints2,gamePoints2 are non valid.
     * @return a long the packed version of the scores of both team. 
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2, int gamePoints2) {
        checkArgument( areArgumentsValid(turnTricks1,turnPoints1,gamePoints1) && areArgumentsValid(turnTricks2,turnPoints2,gamePoints2));
        
        int t1Score = pack32(turnTricks1,turnPoints1,gamePoints1);
        int t2Score = pack32(turnTricks2, turnPoints2, gamePoints2);
        
        return Bits64.pack(t1Score, Integer.SIZE, t2Score, Integer.SIZE);
    }
    
    private static final int TRICK_COUNT_INDEX = 0;
    private static final int TURN_PTS_INDEX = 4;
    private static final int GAME_PTS_INDEX = 13;
    
    private static final int TRICK_COUNT_SIZE = 4;
    private static final int TURN_PTS_SIZE = 9;
    private static final int GAME_PTS_SIZE = 11;
    private static final int UNUSED_SIZE = 8;
    
    private static final int INITIAL_TRICK_COUNT = 0;
    private static final int INITIAL_TURN_POINTS = 0;
    private static final int MAX_TURN_PTS = 257;
    private static final int MAX_GAME_PTS = 2000;
    
    
    
    /**
     * Check if the value of the packed score is valid.
     * @param pkScore a long the packed version of a score.
     * @return a boolean true if the given packed score is valid.
     */
    public static boolean isValid(long pkScore) {
        long nbTricks1 = Bits64.extract(pkScore, TRICK_COUNT_INDEX, TRICK_COUNT_SIZE);
        long nbTricks2 = Bits64.extract(pkScore, Integer.SIZE + TRICK_COUNT_INDEX, TRICK_COUNT_SIZE);
        
        long turnPts1 = Bits64.extract(pkScore, TURN_PTS_INDEX, TURN_PTS_SIZE);
        long turnPts2 = Bits64.extract(pkScore, Integer.SIZE + TURN_PTS_INDEX, TURN_PTS_SIZE);
        
        long gamePts1 = Bits64.extract(pkScore, GAME_PTS_INDEX, GAME_PTS_SIZE);
        long gamePts2 = Bits64.extract(pkScore, Integer.SIZE + GAME_PTS_INDEX, GAME_PTS_SIZE);
        
        long unusedBits1 = Bits64.extract(pkScore, GAME_PTS_INDEX + GAME_PTS_SIZE, UNUSED_SIZE);
        long unusedBits2 = Bits64.extract(pkScore, Integer.SIZE + GAME_PTS_INDEX + GAME_PTS_SIZE, UNUSED_SIZE);

        return ((nbTricks1 <= Jass.TRICKS_PER_TURN)
                && (turnPts1 <= MAX_TURN_PTS) 
                && (gamePts1 <= MAX_GAME_PTS)
                && (unusedBits1 == 0)
                && (nbTricks2 <= Jass.TRICKS_PER_TURN)
                && (turnPts2 <= MAX_TURN_PTS) 
                && (gamePts2 <= MAX_GAME_PTS)
                && (unusedBits2 == 0));
    }
    
    /**
     * Gives the number of {@link Trick}s made by a given team in the current turn.
     * @param pkScore a long the packed version of a {@link Score}.
     * @param t a {@link TeamId} the team of which we want the number of {@link Trick}s.
     * @return an int the number of {@link Trick}s gained by team t during the current turn.
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid(pkScore): "Invalid pkScore in turnTricks function of PackedScore";
        
        return t == TeamId.TEAM_1 ?
                (int) Bits64.extract(pkScore, TRICK_COUNT_INDEX, TRICK_COUNT_SIZE) :
                    (int) Bits64.extract(pkScore, Integer.SIZE + TRICK_COUNT_INDEX, TRICK_COUNT_SIZE);
    }
    
    /**
     * Gives the number of points made by a given team in the current turn.
     * @param pkScore a long the packed version of a {@link Score}.
     * @param t a {@link TeamId} the team of which we want the number of points made durring the turn.
     * @return an int the number of points gained by team t during the current turn.
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid(pkScore): "Invalid pkScore in turnPoints function of PackedScore";
        
        return t == TeamId.TEAM_1 ?
                (int) Bits64.extract(pkScore, TURN_PTS_INDEX, TURN_PTS_SIZE) :
                    (int) Bits64.extract(pkScore, Integer.SIZE + TURN_PTS_INDEX, TURN_PTS_SIZE);
    }
    
    /**
     * Gives the number of points made by a given team before the current turn.
     * @param pkScore a long the packed version of a {@link Score} .
     * @param t a {@link TeamId} the team of which we want the number of points.
     * @return an int the number of points gained by team t before the current turn.
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid(pkScore): "Invalid pkScore in gamePoints function of PackedScore";
        
        return t == TeamId.TEAM_1 ?
                (int) Bits64.extract(pkScore, GAME_PTS_INDEX, GAME_PTS_SIZE) :
                    (int) Bits64.extract(pkScore, Integer.SIZE + GAME_PTS_INDEX, GAME_PTS_SIZE);
    }
    
    /**
     * Gives the number of points made by a given team in the current turn and before.
     * @param pkScore a long the packed version of a {@link Score}.
     * @param t a {@link TeamId} the team of which we want the number of points.
     * @return an int the number of points gained by team t during the current turn and before.
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert isValid(pkScore): "Invalid pkScore in totalPoints function of PackedScore";
        
        return t == TeamId.TEAM_1 ?
                gamePoints(pkScore,TeamId.TEAM_1) + turnPoints(pkScore,TeamId.TEAM_1) :
                    gamePoints(pkScore,TeamId.TEAM_2) + turnPoints(pkScore,TeamId.TEAM_2);
    }
    
    /**
     * Computes the {@link Score} of the team after a {@link Trick} has been played.
     * @param pkScore the packed version of a {@link Score}.
     * @param winningTeam a {@link TeamId} the team that won the {@link Trick}.
     * @param trickPoints the amount of points the {@link Trick} is worth.
     * @throws IllegalArgumentException if trickPoints is negative.
     * @return a long the updated {@link packedScore}.
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        assert isValid(pkScore): "Invalid pkScore in withAdditionalTrick function of PackedScore";
        checkArgument(trickPoints >=0);
        
        int winningTurnTricks = turnTricks(pkScore,winningTeam) + 1;
        int winningTurnPoints = turnPoints(pkScore,winningTeam) + trickPoints; 
        int winningGamePoints = gamePoints(pkScore,winningTeam);
       
        int otherTurnTricks = turnTricks(pkScore,winningTeam.other());
        int otherTurnPoints = turnPoints(pkScore,winningTeam.other());
        int otherGamePoints = gamePoints(pkScore,winningTeam.other());
        
        if(winningTurnTricks == Jass.TRICKS_PER_TURN) 
            winningTurnPoints += Jass.MATCH_ADDITIONAL_POINTS;
        
        return winningTeam == TeamId.TEAM_1 ? 
                pack(winningTurnTricks, winningTurnPoints, winningGamePoints, otherTurnTricks, otherTurnPoints, otherGamePoints) :
                    pack(otherTurnTricks, otherTurnPoints, otherGamePoints, winningTurnTricks, winningTurnPoints, winningGamePoints);
    }
    
    /**
     * Gives the packed version of the {@link Score} at the beginning of the next turn.
     * @param pkScore the packed version of the {@link Score}.
     * @return a long the updated {@link packedScore} for the next turn.
     */
    public static long nextTurn(long pkScore) {
        assert isValid(pkScore): "Invalid pkScore in nextTurn function of PackedScore";
        
        return pack(INITIAL_TRICK_COUNT, INITIAL_TURN_POINTS, totalPoints(pkScore,TeamId.TEAM_1),
                INITIAL_TRICK_COUNT, INITIAL_TURN_POINTS, totalPoints(pkScore,TeamId.TEAM_2));
    }
    
    /**
     * Gives a textual representation of the {@link Score}.
     * @param pkScore a long the packed version of the {@link Score}.
     * @return a string the representation of a {@link Score}.
     */
    public static String toString(long pkScore) {
        assert isValid(pkScore): "Invalid pkScore in toString function of PackedScore";
        
        return ("(" + Integer.toUnsignedString(turnTricks(pkScore,TeamId.TEAM_1)) + 
                "," + Integer.toUnsignedString(turnPoints(pkScore,TeamId.TEAM_1)) + 
                "," + Integer.toUnsignedString(gamePoints(pkScore,TeamId.TEAM_1)) + 
                ")/(" + Integer.toUnsignedString(turnTricks(pkScore,TeamId.TEAM_2)) + 
                "," + Integer.toUnsignedString(turnPoints(pkScore,TeamId.TEAM_2)) +
                "," + Integer.toUnsignedString(gamePoints(pkScore,TeamId.TEAM_2)) + ")"); 
    }
    
    private static int pack32(int turnTricks, int turnPoints, int gamePoints) {
        return Bits32.pack(turnTricks, TRICK_COUNT_SIZE, turnPoints, TURN_PTS_SIZE, gamePoints, GAME_PTS_SIZE);
    }
    
    private static boolean areArgumentsValid(int turnTricks, int turnPoints, int gamePoints) {
        return ((turnTricks >= 0) && (turnTricks <= Jass.TRICKS_PER_TURN) &&
                (turnPoints >= 0) && (turnPoints <= MAX_TURN_PTS) &&
                (gamePoints >= 0) && (gamePoints <= MAX_GAME_PTS));
    }
}
