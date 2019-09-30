package javass.jass;

/**
 * An interface containing the main variables defining a Jass game
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public interface Jass {
    /**
     * Number of cards in one hand at the beginning of a round.
     */
    int HAND_SIZE = 9;
    /**
     * Number of tricks in a round.
     */
    int TRICKS_PER_TURN = 9;
    /**
     * Number of necessary points to win.
     */
    int WINNING_POINTS = 1000;
    /**
     * Number of additional points won by a team which has won all the tricks of a round.
     */
    int MATCH_ADDITIONAL_POINTS = 100;
    /**
     * Number of additional points won by a team winning the last trick.
     */
    int LAST_TRICK_ADDITIONAL_POINTS = 5; 
}
