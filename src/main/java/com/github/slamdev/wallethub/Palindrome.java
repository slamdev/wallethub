package com.github.slamdev.wallethub;

import static java.lang.System.*;
import static java.util.stream.IntStream.range;

public class Palindrome {

    static final String USAGE_MESSAGE = "You should pass string as a first argument.";

    public static void main(String[] args) {
        if (args.length == 0) {
            err.println(USAGE_MESSAGE);
            exit(1);
        }
        out.println(isStringPalindrome(args[0]));
    }

    /**
     * Complexity: O(n)
     */
    private static boolean isStringPalindrome(String string) {
        return string.isEmpty() || range(0, string.length() / 2)
                .allMatch(i -> string.charAt(i) == string.charAt(string.length() - i - 1));
    }
}
