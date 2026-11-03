
package com.ftms.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class PasswordUtil {

    private static final int ITERATIONS = 600000;

    public static String hash(char[] password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password, salt, ITERATIONS, 256);

        try {
            byte[] result = SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();

            return "pbkdf2$" + ITERATIONS + "$" +
                    Base64.getEncoder().encodeToString(salt) +
                    "$" + Base64.getEncoder().encodeToString(result);
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean verify(char[] password, String stored)
            throws Exception {

        if (stored == null) return false;

        if (!stored.startsWith("pbkdf2$")) {
            byte[] a = new String(password).getBytes(
                    StandardCharsets.UTF_8);
            byte[] b = stored.getBytes(StandardCharsets.UTF_8);
            return MessageDigest.isEqual(a, b);
        }

        String[] parts = stored.split("\\$");

        if (parts.length != 4) return false;

        int iterations = Integer.parseInt(parts[1]);

        if (iterations < 10000 || iterations > 1000000) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expected = Base64.getDecoder().decode(parts[3]);

        PBEKeySpec spec = new PBEKeySpec(
                password, salt, iterations, expected.length * 8);

        try {
            byte[] actual = SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();

            boolean matches =
                    MessageDigest.isEqual(actual, expected);
            Arrays.fill(actual, (byte) 0);
            return matches;
        } finally {
            spec.clearPassword();
        }
    }
}
