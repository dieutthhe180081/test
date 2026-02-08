package com.sep490.g28.hvh.be.util;

public class StringNormalizeUtil {
    // singlton
    private StringNormalizeUtil() {}

    /**
     * Normalize a Vietnamese personal name.
     * Rules:
     * - Trim leading and trailing whitespaces
     * - Collapse multiple spaces into a single space
     * - Capitalize the first letter of each word
     * - Convert remaining letters to lowercase
     * - Preserve Vietnamese diacritics
     *
     * @param input raw name input from user
     * @return normalized name, or null if input is null
     */
    public static String normalizeVietnameseName(String input) {
        // Return immediately if input is null
        if (input == null) return null;

        // Trim and normalize whitespace
        String s = input.trim().replaceAll("\\s+", " ");

        // Return empty string if nothing remains after trimming
        if (s.isEmpty()) return s;

        StringBuilder result = new StringBuilder();

        // Split name into individual words
        String[] words = s.split(" ");

        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (w.isEmpty()) continue;

            // Convert the whole word to lowercase
            String lower = w.toLowerCase();

            // Capitalize the first character and keep the rest lowercase
            String normalized =
                    Character.toUpperCase(lower.charAt(0)) + lower.substring(1);

            // Append space between words
            if (i > 0) result.append(' ');
            result.append(normalized);
        }

        return result.toString();
    }
}
