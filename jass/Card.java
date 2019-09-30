package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.epfl.javass.Preconditions.*;

/**
 * The representation of a {@link Card} by a {@link Color} and a {@link Rank}.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class Card {
    
    /**
     * Creates a {@link Card} object of a given {@link Rank} and a given {@link Color}.
     * @param c the {@link Color} of the {@link Card} wanted
     * @param r the {@link Rank} of the {@link Card} wanted
     * @return an object {@link Card}, representing a {@link Card} with the given {@link Rank} and the given {@link Color}
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }
    
    /**
     * Creates a {@link Card} object from the packed version of a card.
     * @param packed an int, the packed version of a card.
     * @throws IllegalArgumentException if isValid(packed) is false.
     * @return an object {@link Card}, representing the given packed version 
     */
    public static Card ofPacked(int packed) {
        checkArgument(PackedCard.isValid(packed)); 
        
        return new Card(packed);
    }

    private final int packed;

	private Card(int packed) {
	    this.packed = packed;
	}
	
	/**
	 * An enumeration of the possible colors of a {@link Card}
	 * @author Charles BEAUVILLE
	 * @author Celia HOUSSIAUX
	 *
	 */
	public enum Color {
        SPADE("\u2660"), HEART("\u2661"), DIAMOND("\u2662"), CLUB("\u2663");
 
	    /**
         * an int the total number of colors 
         */
        public static final int COUNT = 4;
        /**
         * a list containing all the {@link Color} values
         */
        public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));
	    
        private final String symbole;
 
        private Color(String symbole) {
            this.symbole = symbole;
        }
        
        @Override
        public String toString() { //redefinition of the Object class' toString method which gives us the card's color.
            return symbole;
        }
 
    }
 
	/**
	 * An enumeration of the possible ranks of a {@link Card}
	 * @author Charles BEAUVILLE
	 * @author Celia HOUSSIAUX
	 *
	 */
    public enum Rank {
 
        SIX("6"),SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), JACK("J"), QUEEN("Q"),KING("K"), ACE("A");
 
        /**
         * an int the total number of ranks 
         */
        public static final int COUNT = 9;
        /**
         * a list containing all the {@link Rank} values
         */
        public static final List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));
 
        
        private final String rang;
 
        private Rank(String rang) {
            this.rang = rang;
        }
 
        /**
         * Gives the order of the {@link Rank} while the color is trump. 
         * @return an int the {@link Rank} index when the color is trump.
         */
        public int trumpOrdinal() {
            switch (this) {
            case SIX:
                return 0;
            case SEVEN:
                return 1;
            case EIGHT:
                return 2;
            case NINE:
                return 7;
            case TEN:
                return 3;
            case JACK:
                return 8;
            case QUEEN:
                return 4;
            case KING:
                return 5;
            case ACE:
                return 6;
            default:
                return -1;
            }
        }
        
        @Override
        public String toString() {
            return rang;
        }
 
    }

	/**
	 * Returns the packed version of this {@link Card}.
	 * @return an int the packed version of this {@link Card}.
	 */
    public int packed() {
        return packed;
    }

    /**
     * Returns the color of the {@link Card}. 
     * @return The {@link Color} of this {@link Card}
     */
    public Color color(){
        return PackedCard.color(packed);
    }

    /**
     * Returns the rank of the {@link Card}.
     * @return The {@link Rank} of this {@link Card}
     */
    public Rank rank(){
        return PackedCard.rank(packed);
    }
    /**
     * Returns true only if the card to which the function is applied is superior to the second argument of the function, knowing the trump {@link Color}.
     * @param the trump {@link Color} of a the turn
     * @param that the {@link Card} to compared with the caller of the function
     * @return a boolean, whether or not this {@link Card} a better {@link Card} than the card argument
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, packed, that.packed());
    }
 
    /**
     * Returns the value of this {@link Card}, knowing the current trump of the turn.
     * @param the trump a {@link Color}, of the turn
     * @return an int the number of points this {@link Card} is worth
     */
    public int points(Color trump) {         
        return PackedCard.points(trump, packed);
    }
 
    @Override
    public boolean equals(Object that0) {
        return that0 != null 
                && that0.getClass() == this.getClass() 
                && this.color() == ((Card)that0).color() 
                && this.rank() == ((Card)that0).rank();
    }
    
    @Override
    public int hashCode() {
        return packed;
    }
    
    @Override
    public String toString() {
        return PackedCard.toString(packed);
    }
}

		

