package com.github.slamdev.wallethub;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.System.*;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.*;

public class TopPhrases {

    static final String USAGE_MESSAGE = "You should pass number as a first argument, "
            + "string as a second argument "
            + "and path to the file as a third argument";

    public static void main(String[] args) {
        if (args.length < 3) {
            exitWithUsageMessage();
        }
        int top = toInt(args[0]);
        String pipe = args[1];
        Path file = toFile(args[2]);
        out.println(getTopPhrases(file, top, pipe));
    }

    /**
     * Time complexity: O(n)
     * Space complexity: O(n)
     */
    private static Map<String, Long> getTopPhrases(Path file, int top, String pipe) {
        Pattern pattern = compile(quote(pipe));
        Map<String, Long> allPhrases;
        try (Stream<String> lines = lines(file)) {
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

    private static int toInt(String string) {
        try {
            return parseInt(string);
        } catch (NumberFormatException e) {
            exitWithUsageMessage();
        }
        return 0;
    }

    private static Path toFile(String string) {
        Path file = get(string);
        if (!exists(file)) {
            exitWithUsageMessage();
        }
        return file;
    }

    private static void exitWithUsageMessage() {
        err.println(USAGE_MESSAGE);
        exit(1);
    }

    private static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
