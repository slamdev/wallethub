package com.github.slamdev.wallethub;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PalindromeTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final SystemErrRule error = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule out = new SystemOutRule().enableLog();

    @Test
    public void should_display_usage_message_when_no_argument_passed() {
        exit.expectSystemExit();
        Palindrome.main(params());
        assertThat(error.getLog(), is(Palindrome.USAGE_MESSAGE));
    }

    @Test
    public void should_exit_app_with_no_zero_status_when_no_argument_passed() {
        exit.expectSystemExitWithStatus(1);
        Palindrome.main(params());
    }

    @Test
    public void should_return_true_if_string_is_empty() {
        Palindrome.main(params(""));
        assertThat(out(), is("true"));
    }

    @Test
    public void should_return_true_if_string_contains_one_symbol() {
        Palindrome.main(params("a"));
        assertThat(out(), is("true"));
    }

    @Test
    public void should_return_true_if_even_string_is_palindrome() {
        Palindrome.main(params("abccba"));
        assertThat(out(), is("true"));
    }

    @Test
    public void should_return_false_if_even_string_is_not_palindrome() {
        Palindrome.main(params("abcbca"));
        assertThat(out(), is("false"));
    }

    @Test
    public void should_return_true_if_odd_string_is_palindrome() {
        Palindrome.main(params("abcdcba"));
        assertThat(out(), is("true"));
    }

    @Test
    public void should_return_false_if_odd_string_is_not_palindrome() {
        Palindrome.main(params("abcdbca"));
        assertThat(out(), is("false"));
    }

    private String out() {
        return out.getLog().replaceAll(lineSeparator(), "");
    }

    private String[] params(String... params) {
        return params;
    }
}
