package thedimas.network.util;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * The Bytes class provides utility methods for working with byte arrays.
 */
public class Bytes {

    /**
     * Hashes the input byte array using the SHA-256 algorithm.
     *
     * @param input the input byte array to be hashed
     * @return the hashed byte array
     */
    @SneakyThrows
    public static byte[] hashed(byte[] input) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input);
    }

    /**
     * Combines multiple byte arrays into a single byte array.
     *
     * @param arrays the byte arrays to be combined
     * @return the combined byte array
     */
    public static byte[] combine(byte[]... arrays) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (byte[] array : arrays) {
                outputStream.write(array);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
