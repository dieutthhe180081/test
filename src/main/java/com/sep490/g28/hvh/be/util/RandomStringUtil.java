package com.sep490.g28.hvh.be.util;

import java.security.SecureRandom;

public class RandomStringUtil {
    private static final String CHARSET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String random8AlphaNumeric() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }

    public static String random6Numberic(){
        return String.valueOf(RANDOM.nextInt(900000) + 100000);
    }
}
