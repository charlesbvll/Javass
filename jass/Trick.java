package ch.epfl.javass.jass;

import static ch.epfl.javass.Preconditions.*;

import ch.epfl.javass.jass.Card.Color;

/**
 * The representation of a trick of a jass game.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class Trick {

	/**
	 * The trick representing an invalid trick that is the representation of an empty trick.
	 */
	public final static Trick INVALID = new Trick(PackedTrick.INVALID);
	
	   /**
     * This method gives a brand new empty trick associated with the first player.
     * @param trump
     * @param firstPlayer
     * @return a new empty trick.
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * This method gives the trick based on it packed version.
     * @param packed
     * @throws IllegalArgumentException if packed is not valid
     * @return a the trick of the packed version.
     */
    public static Trick ofPacked(int packed) {
        checkArgument(PackedTrick.isValid(packed));

        return new Trick(packed);
    }

	private final int packed;
	
	private Trick(int packed) {
		this.packed = packed;
	}

	/**
	 * This method gives the packed version of a trick.
	 * @return packed.
	 */
	public int packed() {
		return packed;
	}

	/**
	 * The empty trick that follows (this). It means the trick empty has the same trump color, the following index of (this)
       and the first player winner of (this).
	 * @throws IllegalStateException if packed is the last trick.
	 * @return an empty trick that follows (this).
	 */
	public Trick nextEmpty() {
		if (!isFull())
			throw new IllegalStateException();

		return new Trick(PackedTrick.nextEmpty(packed));
	}

	/**
	 * Checks if the trick is empty meaning no card have been played.
	 * @return true if (this) is empty.
	 */
	public boolean isEmpty() {
		return PackedTrick.isEmpty(packed);
	}

	/**
	 * Checks if the trick is full meaning all cards have been played.
	 * @return true if (this) is full.
	 */
	public boolean isFull() {
		return PackedTrick.isFull(packed);
	}

	/**
	 * Checks if the current trick is the last one of the turn
	 * @return true if (this) is the last trick.
	 */
	public boolean isLast() {
		return PackedTrick.isLast(packed);
	}

	/**
	 * Computes the size of the trick i.e the number of cards that have been played.
	 * @return an int the size of the trick.
	 */
	public int size() {
		return PackedTrick.size(packed);
	}

	/**
	 * Gives the trump color of the trick.
	 * @return a Color the trump color of the trick.
	 */
	public Color trump() {
		return PackedTrick.trump(packed);
	}

	/**
	 * Computes the index of the trick in the current turn.
	 * @return an int the index of the trick.
	 */
	public int index() {
		return PackedTrick.index(packed);
	}

	/**
	 * This method gives the player at the given index.
	 * @param index an int.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 * @return the player of the trick at the given index.
	 */
	public PlayerId player(int index) {
		return PackedTrick.player(packed, checkIndex(index, PlayerId.COUNT));
	}

	/**
	 * Gives the card of the trick at the given index.
	 * @param index an int.
	 * @return the card of the trick at the given index.
	 */
	public Card card(int index) {
		return Card.ofPacked(PackedTrick.card(packed, checkIndex(index, size())));
	}

	/**
	 * This method gives a trick identical as (this) plus the card c.
	 * @param c a card to be added to the trick.
	 * @throws IllegalStateException if packed is full.
	 * @return (this) plus the card c.
	 */
	public Trick withAddedCard(Card c) {
		if (isFull())
			throw new IllegalStateException();

		return new Trick(PackedTrick.withAddedCard(packed, c.packed()));
	}

	/**
	 * This method gives the original color of the trick (the color of the first card played).
	 * @throws IllegalStateException if packed is empty.
	 * @return the color of the first card played.
	 */
	public Color baseColor() {
		if (isEmpty())
			throw new IllegalStateException();

		return PackedTrick.baseColor(packed);
	}

	/**
	 * This method gives a subset of hand representing all the cards that can be played during the next trick. 
	 * @param hand the cardset to check the playable cards in it.
	 * @throws IllegalStateException if packed is full.
	 * @return a subset of hand representing all the cards that can be played by the player during the next trick.
	 */
	public CardSet playableCards(CardSet hand) {
		if (isFull())
			throw new IllegalStateException();

		return CardSet
				.ofPacked(PackedTrick.playableCards(packed, hand.packed()));
	}

	/**
	 * Computes the number of points the trick is worth.
	 * @return an int the number of points the trick is worth.
	 */
	public int points() {
		return PackedTrick.points(packed);
	}

	/**
	 * Gives the winning player of the trick
	 * @return the PlayerId of the player winning the trick.
	 */
	public PlayerId winningPlayer() {
		return PackedTrick.winningPlayer(packed);
	}

	@Override
	public String toString() {
		return PackedTrick.toString(packed);
	}

	@Override
	public boolean equals(Object that0) {
		return that0 != null && that0.getClass() == this.getClass()
				&& this.packed() == ((Trick) that0).packed();
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(packed);
	}

}
