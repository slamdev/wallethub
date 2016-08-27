package com.github.slamdev.wallethub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class TopPhrases {

    static final String USAGE_MESSAGE = "You should pass number as a first argument, "
            + "string as a second argument "
            + "and path to the file as a third argument";

    public static void main(String[] args) {
        if (args.length < 3) {
            exitWithUsageMessage();
        }
        int top = 0;
        try {
            top = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            exitWithUsageMessage();
        }
        String pipe = args[1];
        Path file = Paths.get(args[2]);
        if (!Files.exists(file)) {
            exitWithUsageMessage();
        }
        System.out.println(getTopPhrases(file, top, pipe));
    }

    /**
     * Complexity: O(n)
     */
    private static Map<String, Long> getTopPhrases(Path file, int top, String pipe) {
        Pattern pattern = Pattern.compile(Pattern.quote(pipe));
        Map<String, Long> allPhrases;
        try (Stream<String> lines = Files.lines(file)) {
            allPhrases = lines
                    .flatMap(pattern::splitAsStream)
                    .map(String::trim)
                    .filter(not(String::isEmpty))
                    .collect(groupingBy(w -> w, counting()));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return allPhrases
                .entrySet()
                .stream()
                .filter(e -> e.getValue() >= top)
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private static void exitWithUsageMessage() {
        System.err.println(USAGE_MESSAGE);
        System.exit(1);
    }

    private static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
