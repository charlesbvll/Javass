package javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Methods to serialize object into byte sequences to transmit them in a network.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public final class StringSerializer {

    private static final int HEX_RADIX = 16;
    
    private StringSerializer() {}
    
    /**
     * Serializes a given {@link Integer} into its base 16 textual representation.
     * @param i the {@link Integer} to serialize
     * @return a {@link String} the base 16 representation of the given int.
     */
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, HEX_RADIX);
    }
    
    /**
     * Takes a the string representation of an int and returns the actual {@link Integer}.
     * @param s the string that needs to be deserialized into an {@link Integer}.
     * @return the {@link Integer} represented by the string.
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, HEX_RADIX);
    }
    
    /**
     * Serializes a given {@link Long} into its base 16 textual representation.
     * @param l the {@link Long} to serialize.
     * @return a {@link String} the base 16 representation of the given long.
     */
    public static String serializeLong(long l) {
        return Long.toUnsignedString(l, HEX_RADIX);
    }
    
    /**
     * Takes a the string representation of an int and returns the actual {@link Long}.
     * @param s the string that needs to be deserialized into an {@link Long}.
     * @return the {@link Long} represented by the string.
     */
    public static long deserializeLong(String s) {
        return Long.parseLong(s, HEX_RADIX);
    }
    
    /**
     * Encodes the given string to base 64.
     * @param s the string to encode.
     * @return the encoded string.
     */
    public static String serializeString(String s) {
        Base64.Encoder e = Base64.getEncoder();
        return e.encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Decodes the given string from base 64.
     * @param s the string to decode.
     * @return the decoded string.
     */
    public static String deserializeString(String s) {
        Base64.Decoder d = Base64.getDecoder();
        return new String(d.decode(s), StandardCharsets.UTF_8);
    }
    
    /**
     * Combines the strings given in the array, separated by the given character.
     * @param c the character to separate the strings.
     * @param strings the array of strings to be combined.
     * @return a string constiting of the strings of the array separated by the given character.
     */
    public static String combine(char c, String[] strings) {
        return String.join(String.valueOf(c), strings);
    }
    
    /**
     * Splits at the given character a string into an array of strings. 
     * @param c the character that separates the strings.
     * @param s the string to be split.
     * @return the array of the strings in between the given character.
     */
    public static String[] split(char c, String s) {
        return s.split(String.valueOf(c));
    }
}
