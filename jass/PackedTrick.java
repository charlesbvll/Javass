package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.bits.Bits32;
import static ch.epfl.javass.Preconditions.*;

/**
 * The representation of a trick of a jass game in a packed form that is as an int.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class PackedTrick {
    private PackedTrick() {}
    
	/**
	 * An integer representing an invalid packed {@link Trick} that is an empty {@link Trick}.
	 */
	public static final int INVALID = ~0;

	private static final int LAST_CARD_INDEX = 18;

	private static final int CARD_COLOR_INDEX = 4;
	private static final int CARD_COLOR_SIZE = 2;

	private static final int TRUMP_INDEX = 30;
	private static final int FIRST_PLAYER_INDEX = 28;

	private static final int TRICK_INDEX_POS = 24;
	private static final int TRICK_INDEX_SIZE = 4;

	private static final int FIRST_TRICK_INDEX = 0;
	private static final int LAST_TRICK_INDEX = 8;

	private static final int FIRST_PLAYER_SIZE = 2;
	private static final int TRUMP_SIZE = 2;
	private static final int CARD_SIZE = 6;
	private static final int CARD_NBR = 4;

	/**
	 * Checks if the given {@link PackedTrick} is valid, that is that no card is not invalid while the previous cards are and that it represents a real {@link Trick}.
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @return a boolean whether or not the {@link Trick} is valid.
	 */
	public static boolean isValid(int pkTrick) {
		int trickIndex = Bits32.extract(pkTrick, TRICK_INDEX_POS, TRICK_INDEX_SIZE);
		
		boolean isValid = true;

		for (int i = 0; i <= LAST_CARD_INDEX; i += CARD_SIZE) 
			if(Bits32.extract(pkTrick, i, CARD_SIZE) == PackedCard.INVALID)
				for (int j = i; j <= LAST_CARD_INDEX; j += CARD_SIZE)
					isValid &= Bits32.extract(pkTrick, j, CARD_SIZE) == PackedCard.INVALID;

		return trickIndex < Jass.TRICKS_PER_TURN && isValid;
	}

	/**
	 * Gives the first empty {@link Trick}, meaning with no cards, a given trump and a given first player.
	 * @param trump the {@link Color} of the trump for the {@link Trick}.
	 * @param firstPlayer the {@link PlayerId} of the first player of the {@link Trick}.
	 * @return an integer representing the first empty {@link Trick}.
	 */
	public static int firstEmpty(Color trump, PlayerId firstPlayer) {
		return Bits32.pack(PackedCard.INVALID, CARD_SIZE, 
		        PackedCard.INVALID, CARD_SIZE, 
		        PackedCard.INVALID, CARD_SIZE, 
		        PackedCard.INVALID, CARD_SIZE,
				FIRST_TRICK_INDEX, TRICK_INDEX_SIZE, 
				firstPlayer.ordinal(), FIRST_PLAYER_SIZE, 
				trump.ordinal(), TRUMP_SIZE);
	}

	/**
	 * Gives the next empty {@link Trick}, meaning with the winner of the last {@link Trick} as first player, no {@link Card}, the next index and the same trump.
	 * @param pkTrick an integer representing the packed version of a {@link Trick}.
	 * @return an integer representing a the packed version of the next empty {@link Trick}.
	 */
	public static int nextEmpty(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in nextEmpty function of pkTrick";

		return isLast(pkTrick) ?
		        INVALID :
		            Bits32.pack(PackedCard.INVALID, CARD_SIZE, 
		                    PackedCard.INVALID, CARD_SIZE, 
		                    PackedCard.INVALID, CARD_SIZE, 
		                    PackedCard.INVALID, CARD_SIZE,
		                    index(pkTrick) + 1, TRICK_INDEX_SIZE, 
		                    winningPlayer(pkTrick).ordinal(), FIRST_PLAYER_SIZE, 
		                    trump(pkTrick).ordinal(), TRUMP_SIZE);
	}

	/**
	 * Checks if the trick is the last {@link Trick} of the turn meaning that it's the 9th {@link Trick}.
	 * @param pkTrick an integer representing the packed version of a {@link Trick}.
	 * @return a boolean true if the index of the {@link Trick} is equal to the LAST_TRICK_INDEX.
	 */
	public static boolean isLast(int pkTrick) {
		assert isValid(pkTrick) : "Invalid trick in isLast function of pkTrick";

		return index(pkTrick) == LAST_TRICK_INDEX;
	}

	/**
	 * Checks if the {@link Trick} contains no card meaning it hasn't started.
	 * @param pkTrick an integer representing the packed version of a {@link Trick}.
	 * @return a boolean true if no {@link Card} is in the {@link Trick}.
	 */
	public static boolean isEmpty(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in isEmpty function of pkTrick";

		return Bits32.extract(pkTrick, 0, CARD_SIZE * CARD_NBR) == Bits32.mask(0, CARD_SIZE * CARD_NBR);
	}

	/**
	 * Checks if the {@link Trick} is full meaning that all {@link Card}s have been played.
	 * @param pkTrick an integer representing the packed version of a {@link Trick}.
	 * @return a boolean true if all the cards have been played.
	 */
	public static boolean isFull(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in isFull function of pkTrick";
		
		return size(pkTrick) == TRICK_INDEX_SIZE;
	}

	/**
	 * Computes the size of the {@link Trick}, meaning the number of {@link Card}s that have been played.
	 * @param pkTrick an integer representing the packed version of a {@link Trick}.
	 * @return an integer the number of {@link Card} that have been played.
	 */
	public static int size(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in size function of pkTrick";

		int count = 0;

		for(int i = 0 ; i < PlayerId.COUNT ; ++i)
			if(card(pkTrick, i) != PackedCard.INVALID)
				++ count;

		return count;
	}

	/**
	 * Gives the {@link PlayerId} that played at a given index in the given packed {@link Trick}.
	 * @param pkTrick an integer representing the packed version of a {@link Trick}.
	 * @param index an integer the index of the {@link Card} played by the wanted player.
	 * @throws IndexOutOfBoundsException if the index is bigger than the PlayerId.COUNT or negative
	 * @return the {@link PlayerId} that played the {@link Card} at the given index.
	 */
	public static PlayerId player(int pkTrick, int index) {  
		assert isValid(pkTrick): "Invalid trick in player function of pkTrick";
		
		int i = checkIndex(index, PlayerId.COUNT);
		return PlayerId.ALL.get((firstPlayerIndex(pkTrick) + i) % PlayerId.COUNT);
	}

	/**
	 * A method to get the trump {@link Color} of the {@link Trick}.
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @return the trump {@link Color} of the {@link Trick}.
	 */
	public static Color trump(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in trump function of pkTrick";

		return Color.ALL.get(Bits32.extract(pkTrick, TRUMP_INDEX, TRUMP_SIZE));
	}

	/**
	 * A method to get the index of the {@link Trick}.
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @return an int the index of the given packed trick.
	 */
	public static int index(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in index function of pkTrick";

		return Bits32.extract(pkTrick, TRICK_INDEX_POS, TRICK_INDEX_SIZE);
	}

	/**
	 * A method to get the packed version of the index's {@link Card} in the packedTrick.
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @param index an int the index of the wanted card.
	 * @return the packed version of the index's {@link Card}.
	 */
	public static int card(int pkTrick, int index) {
		assert isValid(pkTrick): "Invalid trick in card function of pkTrick";
		assert (index >= 0 && index <= CARD_NBR): "Invalid index in card function of pkTrick";
 
		return Bits32.extract(pkTrick, index * CARD_SIZE, CARD_SIZE);
	}

	/**
	 * A method that gives the same {@link Trick} that the one given (supposed non empty) with the pkCard added.
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @param pkCard an int the packed representation of a card.
	 * @return pkTrick with pkCard that has been added.
	 */
	public static int withAddedCard(int pkTrick, int pkCard) {
		assert isValid(pkTrick): "Invalid trick in withAddedCard function of pkTrick";
		
		int start = size(pkTrick) * CARD_SIZE;
		
		return pkTrick & ~Bits32.mask(start, CARD_SIZE) | pkCard << start;
	}

	/**
	 * A method to get the initial {@link Color} of the {@link Trick} (the {@link Color} of the first {@link Card}.
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @return the {@link Trick}'s base's {@link Color}.
	 */
	public static Color baseColor(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in baseColor function of pkTrick";

		return Color.ALL.get(Bits32.extract(pkTrick, CARD_COLOR_INDEX, CARD_COLOR_SIZE));
	}

	/**
	 * A method to get the subset in a packed version of the {@link Card} of pkHand which can be played as the next card of the {@link Trick} pkTrick (supposed non empty).
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @param pkHand a long representing the packed version of a card set.
	 * @return a pkCardSet, subset of pkHand.
	 */
	public static long playableCards(int pkTrick, long pkHand) {
		assert isValid(pkTrick): "Invalid trick in playableCards function of pkTrick";
		assert PackedCardSet.isValid(pkHand): "Invalid hand in playableCards function of pkTrick";

		long trumpsAbove = PackedCardSet.EMPTY;
		long trumps = PackedCardSet.subsetOfColor(pkHand, trump(pkTrick));
		long baseColorCards = PackedCardSet.subsetOfColor(pkHand, baseColor(pkTrick));
		long allButTrumps = PackedCardSet.difference(pkHand, trumps);
		long jackSingleton = PackedCardSet
                .singleton(PackedCard
                        .pack(trump(pkTrick), Rank.JACK));

		boolean hasTrumpAbove = false;
		boolean isTrump = false;
		boolean hasNoBaseColor = PackedCardSet.isEmpty(PackedCardSet.intersection(baseColorCards, pkHand));
		boolean hasNoTrump = PackedCardSet.isEmpty(PackedCardSet.intersection(trumps, pkHand));

		int winningCard = card(pkTrick, winningCardIndex(pkTrick));

		if(isEmpty(pkTrick))
			return pkHand;

		//Computes the trump cards of the hand that are better than the current winning card and puts them in the packed card set trumpsAbove.
		for (int i = 0; i < PackedCardSet.size(trumps); i++)
			if(PackedCard.isBetter(trump(pkTrick), PackedCardSet.get(trumps,i), winningCard)) {
				trumpsAbove = PackedCardSet.add(trumpsAbove, PackedCardSet.get(trumps,i));
				hasTrumpAbove = true;
			}

		//Checks if any player has played a trump color card and puts the result in the boolean isTrump.
		for (int j = 0; j < size(pkTrick); j++) 
			if(PackedCard.color(card(pkTrick, j)).equals(trump(pkTrick)))
				isTrump = true;

		//Returns the entire hand if it contains no base color cards and no trump or if the base color is trump and the hand contains the jack of trump.
		if(((!isTrump || hasNoTrump) && hasNoBaseColor) || 
		        (baseColor(pkTrick).equals(trump(pkTrick)) 
		                && PackedCardSet.isEmpty(PackedCardSet.difference(trumps, jackSingleton))))
			return pkHand;

		if(baseColor(pkTrick) != trump(pkTrick))
			if(hasNoBaseColor && !PackedCardSet.isEmpty(allButTrumps))
				return PackedCardSet.union(allButTrumps, trumpsAbove);
			else if(!hasNoBaseColor)
				return PackedCardSet.union(baseColorCards, trumpsAbove);  

		if((baseColor(pkTrick).equals(trump(pkTrick))) || 
		        (hasNoBaseColor && !hasTrumpAbove && PackedCardSet.isEmpty(allButTrumps)))
			return trumps;

		if(hasNoBaseColor && !hasTrumpAbove && !PackedCardSet.isEmpty(allButTrumps))
			return allButTrumps;

		if(hasNoBaseColor && hasTrumpAbove)
			return trumpsAbove;

		return pkHand;
	}

	/**
	 * A method to get the value of the {@link Trick}.
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @return the integer value of the {@link Trick}.
	 */
	public static int points(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in points function of pkTrick";

		int pts = 0;
		
		for (int i = 0; i < CARD_NBR; i++)
		    if(card(pkTrick, i) != PackedCard.INVALID)
	            pts += PackedCard.points(trump(pkTrick), card(pkTrick, i));

		if(isLast(pkTrick))
			pts += Jass.LAST_TRICK_ADDITIONAL_POINTS;

		return pts;
	}

	/**
	 * A method that gives the identity of the current winning {@link PlayerId} of the {@link Trick} (supposed non empty).
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @return the {@link PlayerId} that is winning the current {@link Trick}.
	 */
	public static PlayerId winningPlayer(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in winningPlayer function of pkTrick";

		return player(pkTrick, winningCardIndex(pkTrick));
	}

	/**
	 * Gives a textual representation of a given trick.
	 * @param pkTrick an integer representing a packed {@link Trick}.
	 * @return a String representing the given trick.
	 */
	public static String toString(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in toString function of pkTrick";

		StringBuilder s = new StringBuilder("Pli ")
		        .append(index(pkTrick))
		        .append(", commence par ")
		        .append(player(pkTrick, 0).toString())
		        .append(" : ");

		for (int i = 0; i < size(pkTrick); i++)
			if(i == 0)
				s.append(PackedCard.toString(card(pkTrick, i)));   
			else
				s.append(", ")
				.append(PackedCard.toString(card(pkTrick, i)));         

		return s.toString();
	}

	private static int winningCardIndex(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in winningCardIndex function of pkTrick";

		int winningCardIndex = 0;

		//Compares all the cards of the trick to find the index of the one better that all the others.
		for(int i=0 ; i < size(pkTrick)-1 ; ++i)
			if(PackedCard.isBetter(trump(pkTrick), card(pkTrick, i+1), card(pkTrick, winningCardIndex))) 
				winningCardIndex = (i+1);
			
		return winningCardIndex;
	}

	private static int firstPlayerIndex(int pkTrick) {
		assert isValid(pkTrick): "Invalid trick in firstPlayerIndex function of pkTrick";

		return Bits32.extract(pkTrick, FIRST_PLAYER_INDEX, FIRST_PLAYER_SIZE);
	}

}
