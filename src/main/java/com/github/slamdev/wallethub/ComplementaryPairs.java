package com.github.slamdev.wallethub;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static java.lang.Integer.parseInt;
import static java.lang.System.*;
import static java.util.regex.Pattern.compile;

public class ComplementaryPairs {

    static final String USAGE_MESSAGE = "You should pass number as a first argument "
            + "and list of numbers, separated by comma, as a second argument";

    public static void main(String[] args) {
        if (args.length < 2) {
            exitWithUsageMessage();
        }
        int k = toInt(args[0]);
        int[] array = compile(",")
                .splitAsStream(args[1])
                .map(String::trim)
                .filter(not(String::isEmpty))
                .mapToInt(ComplementaryPairs::toInt)
                .toArray();
        out.println(countComplementaryPairs(k, array));
    }

    /**
     * Complexity: O(n)
     */
    private static int countComplementaryPairs(int sum, int[] array) {
        if (array.length < 2) {
            return 0;
        }
        int count = 0;
        Set<Integer> set = new HashSet<>(array.length);
        for (int value : array) {
            int delta = sum - value;
            if (set.contains(delta)) {
                count++;
            } else {
                set.add(value);
            }
        }
        return count;
    }

    private static int toInt(String string) {
        try {
            return parseInt(string);
        } catch (NumberFormatException e) {
            exitWithUsageMessage();
        }
        return 0;
    }

    private static void exitWithUsageMessage() {
        err.println(USAGE_MESSAGE);
        exit(1);
    }

    private static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
