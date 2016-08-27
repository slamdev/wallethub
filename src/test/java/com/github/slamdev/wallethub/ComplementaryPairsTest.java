package com.github.slamdev.wallethub;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ComplementaryPairsTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final SystemErrRule error = new SystemErrRule().enableLog();

    @Rule
    public final SystemOutRule out = new SystemOutRule().enableLog();

    @Test
    public void should_display_usage_message_when_not_enough_argument_passed() {
        exit.expectSystemExit();
        ComplementaryPairs.main(params("1"));
        assertThat(error.getLog(), is(ComplementaryPairs.USAGE_MESSAGE));
    }

    @Test
    public void should_exit_app_with_no_zero_status_when_not_enough_argument_passed() {
        exit.expectSystemExitWithStatus(1);
        ComplementaryPairs.main(params("1"));
    }

    @Test
    public void should_display_usage_message_when_arguments_not_numbers() {
        exit.expectSystemExit();
        ComplementaryPairs.main(params("a", "b"));
        assertThat(error.getLog(), is(ComplementaryPairs.USAGE_MESSAGE));
    }

    @Test
    public void should_exit_app_with_no_zero_status_when_arguments_not_numbers() {
        exit.expectSystemExitWithStatus(1);
        ComplementaryPairs.main(params("a", "b"));
    }

    @Test
    public void should_return_zero_when_empty_array_passed() {
        ComplementaryPairs.main(params("1", ""));
        assertThat(out(), is("0"));
    }

    @Test
    public void should_return_zero_when_single_element_passed() {
        ComplementaryPairs.main(params("1", "1"));
        assertThat(out(), is("0"));
    }

    @Test
    public void should_return_one_for_array_with_zero_and_element() {
        ComplementaryPairs.main(params("1", "1, 0"));
        assertThat(out(), is("1"));
    }

    @Test
    public void should_return_one_for_equal_pair() {
        ComplementaryPairs.main(params("2", "1, 1"));
        assertThat(out(), is("1"));
    }

    @Test
    public void should_return_one_for_same_negative_and_positive() {
        ComplementaryPairs.main(params("0", "-1, 1"));
        assertThat(out(), is("1"));
    }

    @Test
    public void should_return_count_of_complementary_pairs() {
        ComplementaryPairs.main(new String[]{"4", "2, 5, -1, 6, 10, -2"});
        assertThat(out(), is("2"));
    }

    private String out() {
        return out.getLog().replaceAll("\r\n", "");
    }

    private String[] params(String... params) {
        return params;
    }
}