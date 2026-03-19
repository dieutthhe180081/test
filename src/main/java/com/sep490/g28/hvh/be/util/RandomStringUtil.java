package com.sep490.g28.hvh.be.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomStringUtil {
    private RandomStringUtil() {}

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGIT = "0123456789";
    private static final String ALL = LOWER + UPPER + DIGIT;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String random8AlphaNumeric() {
        List<Character> chars = new ArrayList<>();

        // make sure the policy at least 1 lowercase, 1 uppercase, 1 digit
        chars.add(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        chars.add(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        chars.add(DIGIT.charAt(RANDOM.nextInt(DIGIT.length())));

        // other chars
        while (chars.size() < 8) {
            chars.add(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        // shuffle
        Collections.shuffle(chars, RANDOM);

        StringBuilder sb = new StringBuilder(8);
        chars.forEach(sb::append);
        return sb.toString();
    }

    public static String random6Numberic(){
        return String.valueOf(RANDOM.nextInt(900000) + 100000);
    }
}
