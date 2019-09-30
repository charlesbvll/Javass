package javass.bits;

import static javass.Preconditions.*;

public final class Bits32 {
    private Bits32() {
    }

    /**
     * Creates a bitmask from a start position to a position defined by start +
     * size -1
     * 
     * @param start
     *            the index of the first bit of the bitmask
     * @param size
     *            the length of the bitmask
     * @throws IllegalArgumentException
     *             if start and size don't define a subset of 0 to 31
     * @return the bitmask
     */
    public static int mask(int start, int size) {
        checkStartSize(start, size);
        if (size == Integer.SIZE)
            return ~0;
        else
            return ((1 << size) - 1) << start;
    }

    /**
     * creates a bitstring which has it's size least significant bits equals to
     * those of bits from start to start + size - 1
     * 
     * @param bits
     *            the starting bitstring
     * @param start
     *            the index of the first bit of the bitstring to be extracted
     * @param size
     *            of the bitstring to be extracted
     * @throws IllegalArgumentException
     *             if start and size don't define a subset of 0 to 31
     * @return a bitstring with the exctracted bitstring from bits as its least
     *         significant bits
     */
    public static int extract(int bits, int start, int size) {
        checkStartSize(start, size);
        return (bits >> (start)) & mask(0, size);
    }

    /**
     * Packs two bitstrings(v1 and v2) together in slots of size s1 and s2
     * 
     * @param vi
     *            bitsrings to pack
     * @param si
     *            size of each slot
     * @throws IllegalArgumentException
     *             if vi and si aren't between 1 and 32 and if vi > si and if
     *             vi+si>32
     * @return the final bitstring that packs all of the bitstrings together
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        checkArgument(s1 + s2 <= Integer.SIZE);
        checkPack(v1, s1);
        checkPack(v2, s2);
        return (v2 << (s1)) + v1;
    }

    /**
     * Packs three bitstrings(vi) together in slots of size si (i from 1 to 3)
     * 
     * @param vi
     *            bitsrings to pack
     * @param si
     *            size of each slot
     * @throws IllegalArgumentException
     *             if vi and si aren't between 1 and 32 and if vi > si and if
     *             vi+si>32
     * @return the final bitstring that packs all of the bitstrings together
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        checkArgument(s1 + s2 + s3 <= Integer.SIZE);
        checkPack(v1, s1);
        checkPack(v2, s2);
        checkPack(v3, s3);
        return ((v3 << (s2 + s1)) + (v2 << s1)) + v1;
    }

    /**
     * Packs seven bitstrings(vi) together in slots of size si (i from 1 to 7)
     * 
     * @param vi
     *            bitsrings to pack
     * @param si
     *            size of each slot
     * @throws IllegalArgumentException
     *             if vi and si aren't between 1 and 32 and if vi > si and if
     *             vi+si>32
     * @return the final bitstring that packs all of the bitstrings together
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        checkArgument(s1 + s2 + s3 + s4 + s5 + s6 + s7 <= Integer.SIZE);
        checkPack(v1, s1);
        checkPack(v2, s2);
        checkPack(v3, s3);
        checkPack(v4, s4);
        checkPack(v5, s5);
        checkPack(v6, s6);
        checkPack(v7, s7);
        return (v7 << (s1 + s2 + s3 + s4 + s5 + s6))
                + (v6 << (s1 + s2 + s3 + s4 + s5)) + (v5 << (s1 + s2 + s3 + s4))
                + (v4 << (s1 + s2 + s3)) + (v3 << (s2 + s1)) + (v2 << s1) + v1;
    }

    /**
     * Checks if the arguments of the pack functions are >0 and <Integer.SIZE
     * and if the size of the bitstring is less than the slot it's destined to
     * 
     * @param v
     *            bitstring to check
     * @param s
     *            size to check
     * @throws IllegalArgumentException
     *             if the conditions are not met
     */
    private static void checkPack(int v, int s) {
        if (!(s <= Integer.SIZE && s >= 1 && (mask(0, s) & v) == v))
            throw new IllegalArgumentException();
    }

    /**
     * Checks if a given start position and size are valid meaning they're
     * positive and less than the max value of {@link Integer} or throws an
     * exception.
     * 
     * @param start
     *            an int that must be positive and inferior to the max value of
     *            {@link Integer}.
     * @param size
     *            an int that must be positive and inferior to the max value of
     *            {@link Integer}.
     * @throws IllegalArgumentException
     *             if start and size don't define a subset of 0 to 31.
     */
    private static void checkStartSize(int start, int size) {
        checkArgument(size <= Integer.SIZE && size >= 0 && start >= 0
                && start <= Integer.SIZE && start + size <= Integer.SIZE);
    }
}
