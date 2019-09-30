package ch.epfl.javass.bits;

import static ch.epfl.javass.Preconditions.checkArgument;

public final class Bits64 {   
    private Bits64() {}
    
    /**
     * Creates a bitmask from a start position to a position defined by start + size -1
     * @param start the index of the first bit of the bitmask
     * @param size the length of the bitmask
     * @throws IllegalArgumentException if start and size don't define a subset of 0 to 63
     * @return the bitmask
     */
    public static long mask(int start, int size) {
        checkStartSize(start, size);
        if (size == Long.SIZE)
            return ~0;
        else
            return ((1L << size) - 1) << start;
    }
    
    /**
     * creates a bitstring which has it's size least significant bits equals to those of bits from start to start + size - 1
     * @param bits the starting bitstring
     * @param start the index of the first bit of the bitstring to be extracted
     * @param size of the bitstring to be extracted
     * @throws IllegalArgumentException if start and size don't define a subset of 0 to 63
     * @return a bitstring with the exctracted bitstring from bits as its least significant bits
     */
    public static long extract(long bits, int start, int size) {
        checkStartSize(start, size);
        return (bits >> (start)) & mask(0, size);
    }
    
    /**
     * Packs two bitstrings(v1 and v2) together in slots of size s1 and s2
     * @param vi bitsrings to pack
     * @param si size of each slot
     * @throws IllegalArgumentException if vi and si aren't between 1 and 64 and if vi > si and if vi+si>64
     * @return the final bitstring that packs all of the bitstrings together
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        checkArgument(s1 + s2 <= Long.SIZE);
        checkPack(v1, s1);
        checkPack(v2, s2);
        return (v2 << (s1) ) + v1;
    }
    
    /**
     * Checks if the arguments of the pack functions are >0 and <Long.SIZE and if the size of the bitstring is less than the slot it's destined to
     * @param v bitstring to check
     * @param s size to check
     * @throws IllegalArgumentException if the conditions are not met
     */
    private static void checkPack(long v, int s) {
        if(!(s <= Long.SIZE 
                && s >= 1
                && (mask(0,s) & v)==v ))
            throw new IllegalArgumentException();
    }
    
    /**
     * Checks if a given start position and size are valid 
     * meaning they're positive and less than the max value of {@link Long} or throws an exception.
     * @param start an int that must be positive and inferior to the max value of {@link Long}.
     * @param size an int that must be positive and inferior to the max value of {@link Integer}.
     * @throws IllegalArgumentException if start and size don't define a subset of 0 to 63.
     */
    private static void checkStartSize(int start, int size) {
        checkArgument(size <= Long.SIZE 
                && size >= 0 
                && start >= 0 
                && start <= Long.SIZE 
                && start + size <= Long.SIZE);
    }
}
