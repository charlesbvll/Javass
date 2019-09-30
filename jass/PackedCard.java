package javass.jass;

import javass.bits.Bits32;
import javass.jass.Card.Color;
import javass.jass.Card.Rank;

/**
 * Allows us to manipulate cards from a Jass game packed in an integer.
 * 
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class PackedCard {

    private PackedCard() {
    }

    /**
     * Contains the binary value 111111, which represents an invalid packed card
     */
    public static final int INVALID = 0b111111;

    /**
     * Returns the packed card having the proper {@link Color} and {@link Rank}.
     * 
     * @param c
     *            the {@link Color} of the wanted packed {@link Card}.
     * @param r
     *            the {@link Rank} of the wanted packed {@link Card}.
     * @return the packed {@link Card} version of the given {@link Rank} and
     *         {@link Color}.
     */
    public static int pack(Color c, Rank r) {
        return Bits32.pack(r.ordinal(), RANK_SIZE, c.ordinal(), COLOR_SIZE);
    }

    private static final int RANK_INDEX = 0;
    private static final int COLOR_INDEX = 4;

    private static final int MAX_RANK_VALUE = 8;

    private static final int RANK_SIZE = 4;
    private static final int COLOR_SIZE = 2;
    private static final int UNUSED_SIZE = 25;

    private static final int INDEX_TRUMP = 0;
    private static final int INDEX_NORMAL = 1;

    private static final int[][] PTS = { { 0, 0 }, { 0, 0 }, { 0, 0 },
            { 14, 0 }, { 10, 10 }, { 20, 2 }, { 3, 3 }, { 4, 4 }, { 11, 11 } };

    /**
     * Returns true only if the value is a valid packed {@link Card}, which
     * means if the bits containing the rank contain a value between 0 and
     * 8(included) and if the useless bits are all equal to 0.
     * 
     * @param pkCard
     *            an int representing the packed version of a Jass {@link Card}.
     * @return a boolean true if the packed version of the {@link Card} is valid
     */
    public static boolean isValid(int pkCard) {
        return (Bits32.extract(pkCard, RANK_INDEX, RANK_SIZE) <= MAX_RANK_VALUE
                && Bits32.extract(pkCard, RANK_INDEX, RANK_SIZE) >= 0
                && Bits32.extract(pkCard, RANK_SIZE + COLOR_SIZE,
                        UNUSED_SIZE) == 0);
    }

    /**
     * Returns the {@link Color} of the packed {@link Card}.
     * 
     * @param pkCard
     *            an int representing the packed version of a Jass {@link Card}.
     * @return the {@link Color} of the given packed {@link Card}.
     */
    public static Color color(int pkCard) {
        assert isValid(pkCard) : "Invalid card in color function of pkCard";

        int pkColor = Bits32.extract(pkCard, COLOR_INDEX, COLOR_SIZE);

        return Color.ALL.get(pkColor);
    }

    /**
     * Returns the {@link Rank} of the packed {@link Card}.
     * 
     * @param pkCard
     *            an int representing the packed version of a Jass {@link Card}.
     * @return the {@link Rank} of the given packed {@link Card}.
     */
    public static Rank rank(int pkCard) {
        assert isValid(pkCard) : "Invalid card in rank function of pkCard";

        int pkRank = Bits32.extract(pkCard, RANK_INDEX, RANK_SIZE);

        return Rank.ALL.get(pkRank);
    }

    /**
     * Compares the first {@link Card} entered with the second one, knowing that
     * the {@link Color} is trump.
     * 
     * @param trump
     *            the trump {@link Color}
     * @param pkCardL
     *            the {@link Card} to be compared with
     * @param pkCardR
     *            the {@link Card} to be compared to
     * @return a boolean which is true only if the first {@link Card} entered is
     *         superior to the second one, knowing that trump is the asset.
     */
    public static boolean isBetter(Color trump, int pkCardL, int pkCardR) {
        assert isValid(
                pkCardL) : "Invalid pkCardL in isBetter function of pkCard";
        assert isValid(
                pkCardR) : "Invalid pkCardR in isBetter function of pkCard";

        boolean bothTrump = color(pkCardL).equals(trump) && color(pkCardR).equals(trump);
        boolean betterTrumpRank = rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal();
        boolean betterRank = rank(pkCardL).ordinal() > rank(pkCardR).ordinal();
        boolean bothNotTrump = !color(pkCardL).equals(trump) && !color(pkCardR).equals(trump);
        boolean sameColor = color(pkCardL).equals(color(pkCardR));
        
        if(sameColor)
            return bothTrump ? betterTrumpRank : betterRank;
        else
            return !bothNotTrump ? color(pkCardL).equals(trump) : false;
            
    }

    /**
     * Returns the value of the packed {@link Card}.
     * 
     * @param trump
     *            the trump {@link Color}.
     * @param pkCard
     *            an int representing the packed version of a Jass {@link Card}.
     * @return the value of the given packed {@link Card}.
     */
    public static int points(Color trump, int pkCard) {
        assert isValid(pkCard) : "Invalid card in points function of pkCard";

        return color(pkCard).equals(trump)
                ? PTS[rank(pkCard).ordinal()][INDEX_TRUMP]
                : PTS[rank(pkCard).ordinal()][INDEX_NORMAL];
    }

    /**
     * Returns a representation of the packed {@link Card} under a string of
     * character containing the symbol of the {@link Color} and the shorted name
     * of the {@link Rank}.
     * 
     * @param pkCard
     *            an int representing the packed version of a Jass {@link Card}.
     * @return a string the description of the given packed {@link Card} (its
     *         {@link Rank} and its {@link Color}).
     */
    public static String toString(int pkCard) {
        assert isValid(pkCard) : "Invalid card in toString function of pkCard";

        return color(pkCard).toString() +
                rank(pkCard).toString();
    }

}
