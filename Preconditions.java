package ch.epfl.javass;

/**
 * Gives a set of function to test conditions.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class Preconditions {
	private Preconditions() {}
	
	/**
	 * Checks if a boolean expression is true or throws exception.
	 * @param b boolean expression on the arguments.
	 * @throws IllegalArgumentException if b is false.
	 */
	public static void checkArgument(boolean b) {
		if(!b)
			throw new IllegalArgumentException();
	}
	
	/**
	 * Checks if an index is positive and inferior to a size.
	 * @throws IndexOutOfBoundsException if the index is bigger than the size or negative.
	 * @return the given index if it is valid.
	 */
	public static int checkIndex(int index, int size) {
		if(index < 0 || index >= size) 
			throw new IndexOutOfBoundsException();
		
		return index;
	}
}
