package thedimas.network.util;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class Bytes {
    @SneakyThrows
    public static byte[] hashed(byte[] input) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input);
    }

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
