package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Rank;

import static ch.epfl.javass.bits.Bits64.mask;

import java.util.StringJoiner;

/**
 * The packed representation of a card set
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class PackedCardSet {
    private PackedCardSet() {}
    
    private static final int COLOR_SIZE = 16;

    private static final int SPADES_INDEX = 0;
    private static final int HEARTS_INDEX = 16;
    private static final int DIAMONDS_INDEX = 32;
    private static final int CLUBS_INDEX = 48;
    
	/**
	 * The packed version of an empty {@link CardSet}.
	 */
	public static final long EMPTY = 0L;
	/**
	 * The packed version off a {@link CardSet} containing all {@link Card}s of jass.
	 */
	public static final long ALL_CARDS = mask(CLUBS_INDEX, Rank.COUNT) | mask(DIAMONDS_INDEX, Rank.COUNT) | mask(HEARTS_INDEX, Rank.COUNT) | mask(SPADES_INDEX, Rank.COUNT);
    
	private static final long[] trumpTab = 
		{0b111111110L,
				0b111111100L,
				0b111111000L,
				0b000100000L,
				0b111101000L,
				0b000000000L,
				0b110101000L,
				0b100101000L,
				0b000101000L};

	private static final long[] colorTab = {mask(SPADES_INDEX, Rank.COUNT), mask(HEARTS_INDEX, Rank.COUNT), mask(DIAMONDS_INDEX, Rank.COUNT), mask(CLUBS_INDEX, Rank.COUNT)}; 

	/**
	 * Returns true if and only if the given {@link PackedCard}set is valid, none of the unused bits are equal to 1
	 * @param {@link pkCardSet} the packed version of a {@link CardSet} to be checked
	 * @return a boolean whether or not the given {@link pkCardSet} is valid
	 */
	public static boolean isValid(long pkCardSet) {
		return (pkCardSet | ALL_CARDS) == ALL_CARDS
				&& pkCardSet >= 0
				&& pkCardSet <= ALL_CARDS;			
	}

	/**
	 * Returns the packed version of a {@link CardSet} containing all {@link Card}s of {@link Color} trump above the given {@link packedCard}
	 * @param pkCard the {@link packedCard} version of a {@link Card} you want to find which trumps are above
	 * @return a long which represents the packed version of the {@link CardSet} of trump cards above the given card
	 */
	public static long trumpAbove(int pkCard) {
		assert PackedCard.isValid(pkCard): "Invalid card set in trumpAbove function of pkCardSet"; 

		return trumpTab[PackedCard.rank(pkCard).ordinal()] << PackedCard.color(pkCard).ordinal() * COLOR_SIZE;
	}

	/**
	 * Returns the packed version of the {@link CardSet} containing only the given {@link packedCard}
	 * @param pkCard the packed version of the {@link Card} from which the {@link CardSet} is created
	 * @return a long the packed version of a {@link CardSet} containing the given {@link Card}
	 */
	public static long singleton(int pkCard) { 
		assert PackedCard.isValid(pkCard): "Invalid card set in singleton function of pkCardSet";

		return 1L << pkCard;
	}

	/**
	 * Checks if the given {@link pkCardSet} is empty or not (ie contains no {@link Card})
	 * @param pkCardSet a long representing the {@link CardSet} to check
	 * @return a boolean true only if the {@link CardSet} is empty(ie all his bits are null)
	 */
	public static boolean isEmpty(long pkCardSet) {
		assert isValid(pkCardSet): "Invalid card set in isEmpty function of pkCardSet";

		return pkCardSet == EMPTY;
	}

	/**
	 * Computes the size of the given {@link pkCardSet} (ie the number of {@link Card}s it contains)
	 * @param pkCardSet a long representing the packed version of a {@link CardSet}
	 * @return an int the size of the {@link CardSet} (ie the number of {@link Card}s it contains)
	 */
	public static int size(long pkCardSet) {
		assert isValid(pkCardSet): "Invalid card set in size function of pkCardSet";

		return Long.bitCount(pkCardSet);	
	}

	/**
	 * Gives the packed version of the {@link Card} of the given index in the given {@link CardSet}
	 * @param pkCardSet a long representing the {@link pkCardSet} to get the {@link Card} from
	 * @param index an int, the index of the {@link Card} to extract from the {@link CardSet}
	 * @return  an int representing the packed version of a {@link Card}
	 */
	public static int get(long pkCardSet, int index) {
	    assert isValid(pkCardSet): "Invalid card set in get function of pkCardSet";
        assert (index < size(pkCardSet) && index >= 0): "Invalid index in get function of pkCardSet";

		for (int i = 1; i <= index; i++)
		    pkCardSet = pkCardSet ^ Long.lowestOneBit(pkCardSet);
		
		return Long.numberOfTrailingZeros(pkCardSet);
	}

	/**
	 * Adds a {@link packedCard} to the packed version of a {@link CardSet}
	 * @param pkCardSet a long representing the packed version of a {@link CardSet}
	 * @param pkCard an int representing the packed version of a {@link Card}
	 * @return a long the {@link Card} set after the given {@link Card} has been added
	 */
	public static long add(long pkCardSet, int pkCard) {
		assert isValid(pkCardSet): "Invalid card set in add function of pkCardSet";
		assert PackedCard.isValid(pkCard): "Invalid card in add function of pkCardSet";
		
		return pkCardSet | singleton(pkCard) ;

	}

	/**
	 * Deletes a given {@link packedCard} from a {@link pkCardSet}
	 * @param pkCardSet a long representing the packed version of a {@link CardSet}
	 * @param pkCard an int representing a {@link packedCard}
	 * @return a long the {@link CardSet} after the given {@link Card} has been removed
	 */
	public static long remove(long pkCardSet, int pkCard) {
		assert isValid(pkCardSet): "Invalid card set in remove function of pkCardSet";
		assert PackedCard.isValid(pkCard): "Invalid card in remove function of pkCardSet";

		return pkCardSet & ~singleton(pkCard);
	}

	/**
	 * Checks if the given {@link packedCard} is in the given {@link pkCardSet}
	 * @param pkCardSet a long representing a {@link pkCardSet}
	 * @param pkCard an int representing a {@link packedCard}
	 * @return a boolean true if the given card is in the given {@link CardSet}
	 */
	public static boolean contains(long pkCardSet, int pkCard) {
		assert isValid(pkCardSet): "Invalid card set in contains function of pkCardSet";
		assert PackedCard.isValid(pkCard): "Invalid card in contains function of pkCardSet";

		return (pkCardSet & singleton(pkCard)) != EMPTY;
	}

	/**
	 * Computes the inverse of the given p{@link pkCardSet} meaning all the {@link Card}s that are not in the given {@link CardSet}
	 * @param pkCardSet a long representing a {@link pkCardSet}
	 * @return a long representing the packed version of the {@link CardSet} containing all the {@link Card}s that were not in the given {@link CardSet}
	 */
	public static long complement(long pkCardSet) {
		assert isValid(pkCardSet): "Invalid card set in complement function of pkCardSet";

		return pkCardSet ^ ALL_CARDS ;

	}

	/**
	 * Computes the union of the given {@link CardSet}s meaning the {@link CardSet} which contains all the {@link Card}s from both {@link CardSet}s
	 * @param pkCardSet1 a long representing the packed version of a {@link CardSet}
	 * @param pkCardSet2 a long representing the packed version of a {@link CardSet}
	 * @return a long which represents a set containing all the {@link Card}s for both {@link CardSet}s
	 */
	public static long union(long pkCardSet1, long pkCardSet2) {
		assert isValid(pkCardSet1): "Invalid card set 1 in union function of pkCardSet";
		assert isValid(pkCardSet2): "Invalid card set 2 in union function of pkCardSet";

		return pkCardSet1 | pkCardSet2;
	}

	/**
	 * Computes the intersection of the given {@link CardSet}s meaning the {@link CardSet} which contains the {@link Card}s that are in both {@link CardSet}s
	 * @param pkCardSet1 a long representing the packed version of a {@link CardSet}
	 * @param pkCardSet2 a long representing the packed version of a {@link CardSet}
	 * @return a long which represents a {@link CardSet} containing the {@link Card}s that are in both {@link CardSet}s
	 */
	public static long intersection(long pkCardSet1, long pkCardSet2) {
		assert isValid(pkCardSet1): "Invalid card set 1 in intersection function of pkCardSet";
		assert isValid(pkCardSet2): "Invalid card set 2 in intersection function of pkCardSet";

		return pkCardSet1 & pkCardSet2;
	}

	/**
	 * Computes the difference between the two given {@link CardSet}s meaning the {@link CardSet} which contains the {@link Card}s that are in the first but not in the second one
	 * @param pkCardSet1 a long representing the packed version of a {@link CardSet}
	 * @param pkCardSet2 a long representing the packed version of a {@link CardSet} 
	 * @return a long which represents a {@link CardSet} containing the {@link Card}s that are in the first but not in the second one
	 */
	public static long difference(long pkCardSet1, long pkCardSet2) {
		assert isValid(pkCardSet1): "Invalid card set 1 in difference function of pkCardSet";
		assert isValid(pkCardSet2): "Invalid card set 2 in difference function of pkCardSet";

		return pkCardSet1 & complement(pkCardSet2);
	}

	/**
	 * Computes the packed version of a {@link CardSet} containing only the {@link Card}s of the given {@link Color}.
	 * @param pkCardSet a long representing the packed version of a {@link CardSet} 
	 * @param color the {@link Color} of the wanted subset of the {@link CardSet}.
	 * @return a long the packed version of a {@link CardSet} containing only the {@link Card}s of the given {@link Color}.
	 */
	public static long subsetOfColor(long pkCardSet, Card.Color color) {
		assert isValid(pkCardSet): "Invalid card set in subsetOfColor function of pkCardSet";

		return pkCardSet & colorTab[color.ordinal()] ;
	}
	
	/**
	 * Gives a textual representation of a set of cards
	 * @param pkCardSet a long representing the packed version of a set of cards
	 * @return a string describing the given packed set of cards
	 */
	public static String toString(long pkCardSet) {
		assert isValid(pkCardSet): "Invalid card set in toString function of pkCardSet";
		
	    StringJoiner sJ = new StringJoiner(",", "{", "}");	
	    
		for(int i = 0; i < size(pkCardSet); ++i)
			sJ.add(PackedCard.toString(get(pkCardSet, i)));
			
		return sJ.toString();
	}
}
