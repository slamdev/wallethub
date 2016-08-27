package com.github.slamdev.wallethub;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Stream.of;

public class ComplementaryPairs {

    static final String USAGE_MESSAGE = "You should pass number as a first argument "
            + "and list of numbers, separated by comma, as a second argument";

    public static void main(String[] args) {
        if (args.length < 2) {
            exitWithUsageMessage();
        }
        try {
            int k = Integer.parseInt(args[0]);
            int[] array = of(args[1])
                    .map(s -> s.split(","))
                    .flatMap(Arrays::stream)
                    .map(String::trim)
                    .filter(not(String::isEmpty))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            System.out.println(countComplementaryPairs3(k, array));
        } catch (NumberFormatException e) {
            exitWithUsageMessage();
        }
    }

    /**
     * Complexity: O(n)
     */
    private static int countComplementaryPairs3(int sum, int[] number) {
        if (number.length < 2) {
            return 0;
        }
        Set<Integer> set = new HashSet<>(number.length);
        int count = 0;
        for (int value : number) {
            int delta = sum - value;
            if (set.contains(delta)) {
                count++;
            } else {
                set.add(value);
            }
        }
        return count;
    }

    private static void exitWithUsageMessage() {
        System.err.println(USAGE_MESSAGE);
        System.exit(1);
    }

    private static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
