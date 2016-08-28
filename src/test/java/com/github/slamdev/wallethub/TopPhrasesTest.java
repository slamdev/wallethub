package com.github.slamdev.wallethub;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static java.lang.String.valueOf;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TopPhrasesTest {

    private static final String PIPE = "|";

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final SystemErrRule error = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule out = new SystemOutRule().enableLog();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void should_return_empty_map_when_empty_file_passed() {
        TopPhrases.main(params("1", PIPE, createTempFile("")));
        assertThat(out(), is("{}"));
    }

    @Test
    public void should_return_empty_map_when_no_words_greater_than_top() {
        TopPhrases.main(params("2", PIPE, createTempFile("A", "B")));
        assertThat(out(), is("{}"));
    }

    @Test
    public void should_return_map_with_words_equals_or_greater_than_top() {
        TopPhrases.main(params("2", PIPE, createTempFile(join("A", "B"), join("A", "B"), "A")));
        assertThat(out(), is("{A=3, B=2}"));
    }

    @Test
    @Ignore
    public void should_process_huge_file_line_by_line() {
        TopPhrases.main(params("100000", PIPE, createHugeTempFile()));
        assertThat(out(), not(""));
    }

    @Test
    public void should_display_usage_message_when_not_enough_argument_passed() {
        exit.expectSystemExit();
        TopPhrases.main(params("a", "b"));
        assertThat(error.getLog(), is(TopPhrases.USAGE_MESSAGE));
    }

    @Test
    public void should_exit_app_with_no_zero_status_when_not_enough_argument_passed() {
        exit.expectSystemExitWithStatus(1);
        TopPhrases.main(params("a", "b"));
    }

    @Test
    public void should_display_usage_message_when_first_argument_not_number() {
        exit.expectSystemExit();
        TopPhrases.main(params("a", "b", "c"));
        assertThat(error.getLog(), is(TopPhrases.USAGE_MESSAGE));
    }

    @Test
    public void should_exit_app_with_no_zero_status_when_first_argument_not_number() {
        exit.expectSystemExitWithStatus(1);
        TopPhrases.main(params("a", "b", "c"));
    }

    @Test
    public void should_display_usage_message_when_third_argument_not_existing_file() {
        exit.expectSystemExit();
        TopPhrases.main(params("1", "b", "definitely_not_exists"));
        assertThat(error.getLog(), is(TopPhrases.USAGE_MESSAGE));
    }

    @Test
    public void should_exit_app_with_no_zero_status_when_third_argument_not_existing_file() {
        exit.expectSystemExitWithStatus(1);
        TopPhrases.main(params("1", "b", "definitely_not_exists"));
    }

    private String join(String... content) {
        return String.join(PIPE, (CharSequence[]) content);
    }

    private String createTempFile(String... content) {
        try {
            File file = temporaryFolder.newFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(String.join(lineSeparator(), (CharSequence[]) content));
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String createHugeTempFile() {
        Path file = Paths.get(createTempFile());
        BigDecimal d = new BigDecimal(1024);
        // 10 GB
        long desiredSize = BigDecimal.TEN.multiply(d).multiply(d).multiply(d).longValue();
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            while (Files.size(file) < desiredSize) {
                bw.write(hugeChunk() + lineSeparator());
                bw.flush();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return file.toString();
    }

    private String hugeChunk() {
        return range(0, 100000).mapToObj((e) -> join(randomStrings())).collect(joining(lineSeparator()));
    }

    private String[] randomStrings() {
        List<String> letters = rangeClosed('A', 'Z').mapToObj(c -> valueOf((char) c)).collect(toList());
        Random random = new Random();
        return random.ints(5, 0, letters.size()).mapToObj(letters::get).toArray(String[]::new);
    }

    private String out() {
        return out.getLog().replaceAll(lineSeparator(), "");
    }

    private String[] params(String... params) {
        return params;
    }
}