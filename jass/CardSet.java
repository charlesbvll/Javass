package ch.epfl.javass.jass;

import java.util.List;

import static ch.epfl.javass.Preconditions.*;

/**
 * The representation of a {@link CardSet} by a list of {@link Card}.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class CardSet {

	/**
	 * An empty {@link CardSet}.
	 */
	public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
	/**
	 * A {@link CardSet} containing all {@link Card} of a Jass.
	 */
	public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);
	
	 /**
     * A method to get a {@link CardSet} contained in a list of cards.
     * @param cards.
     * @return a packed version of the {@link CardSet}.
     */
    public static CardSet of(List<Card> cards) {
        long packed = PackedCardSet.EMPTY;
        for(Card card : cards) {
            packed = PackedCardSet.add(packed, card.packed());
        }
        return new CardSet(packed);
    }

    /**
     * A method to get a {@link CardSet} knowing its packed version.
     * @param packed, the packed version of the {@link CardSet}.
     * @return a {@link CardSet}.
     */
    public static CardSet ofPacked(long packed) {
        checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }
    
    
	private final long packed;

	private CardSet(long packed) {
		this.packed = packed;
	}

	/**
	 * A method to get the packed version of a {@link CardSet}.
	 * @return a long representing the packed version of the {@link CardSet}.
	 */
	public long packed() {
		return this.packed;
	}

	/**
	 * A method to check if the {@link CardSet} contains no {@link Card}.
	 * @return a boolean true if the {@link CardSet} is empty.
	 */
	public boolean isEmpty() {
		return PackedCardSet.isEmpty(packed);
	}

	/**
	 * A method to get the number of {@link Card} in the {@link CardSet}.
	 * @return an int representing the size of the {@link CardSet}.
	 */
	public int size() {
		return PackedCardSet.size(packed);
	}

	/**
	 * A method to obtain the {@link Card} in the {@link CardSet} at the given index.
	 * @param index an int for the index of the wanted {@link Card}
	 * @return a {@link Card} in the {@link CardSet} at the given index.
	 */
	public Card get(int index) {
		return Card.ofPacked(PackedCardSet.get(packed, index));
	}

	/**
	 * Adds the given {@link Card} to the {@link CardSet}.
	 * @param card the {@link Card} to add to the {@link CardSet}.
	 * @return the new {@link CardSet} containing the {@link Card}.
	 */
	public CardSet add(Card card) {
		return new CardSet(PackedCardSet.add(packed, card.packed()));
	}

	/**
	 * Removes the given {@link Card} from the {@link CardSet}.
	 * @param card the {@link Card} to remove from the {@link CardSet}.
	 * @return the new {@link CardSet} without the given {@link Card}.
	 */
	public CardSet remove(Card card) {
		return new CardSet(PackedCardSet.remove(packed, card.packed()));
	}

	/**
	 * Checks if the {@link CardSet} contains the given {@link Card}.
	 * @param card the {@link Card} to be checked.
	 * @return whether or not the {@link Card} is contained in {@link CardSet}.
	 */
	public boolean contains(Card card) {
		return PackedCardSet.contains(packed, card.packed());
	}

	/**
	 * Computes all the {@link Card}s not contained in {@link CardSet} in form of a new {@link CardSet}.
	 * @return a new {@link CardSet} containing all the {@link Card} not contained in the {@link CardSet}.
	 */
	public CardSet complement() {
		return new CardSet(PackedCardSet.complement(packed));
	}

	/**
	 * Computes the {@link CardSet} containing the {@link Card}s that are in the given {@link CardSet} and in the prior {@link CardSet}.
	 * @param that the given {@link CardSet} to merge with the prior {@link CardSet}.
	 * @return a new {@link CardSet} which is the union of the prior {@link CardSet} and the given {@link CardSet}.
	 */
	public CardSet union(CardSet that) {
		return new CardSet(PackedCardSet.union(packed, that.packed()));
	}

	/**
	 * Computes the {@link CardSet} containing the {@link Card}s that are only in both the given {@link CardSet} and the prior {@link CardSet}.
	 * @param that the given {@link CardSet} to intersect with the prior {@link CardSet}.
	 * @return a new {@link CardSet} which is the intersection of the prior {@link CardSet} and the given {@link CardSet}.
	 */
	public CardSet intersection(CardSet that) {
		return new CardSet(PackedCardSet.intersection(packed, that.packed()));
	}

	/**
	 * Computes the {@link CardSet} containing the {@link Card}s of the {@link CardSet} minus the {@link Card}s of the given {@link CardSet}.
	 * @param that the given {@link CardSet} to be substracted from the prior {@link CardSet}.
	 * @return a new {@link CardSet} which is the {@link CardSet} minus the given {@link CardSet}.
	 */
	public CardSet difference(CardSet that) {
		return new CardSet(PackedCardSet.difference(packed, that.packed()));
	}

	/**
	 * Computes a {@link CardSet} containing only the {@link Card}s of a given {@link Color}.
	 * @param color the {@link Color} of the wanted subset of {@link CardSet}.
	 * @return a {@link CardSet} containing only the {@link Card}s of a given {@link Color}.
	 */
	public CardSet subsetOfColor(Card.Color color) {
		return new CardSet(PackedCardSet.subsetOfColor(packed, color));
	}

	@Override
	public boolean equals(Object that) {
		return that != null 
		        && that.getClass() == this.getClass() 
		        && this.packed == ((CardSet)that).packed();
	}

	@Override
	public int hashCode() {
		return Long.hashCode(packed);
	}

	@Override
	public String toString() {
		return PackedCardSet.toString(packed);
	}
}
