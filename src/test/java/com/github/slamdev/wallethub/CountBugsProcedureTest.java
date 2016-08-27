package com.github.slamdev.wallethub;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.util.Properties;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.skife.jdbi.v2.util.IntegerColumnMapper.PRIMITIVE;

public class CountBugsProcedureTest {

    private static Handle handle;

    @Test
    public void should_return_zero_when_bug_not_in_range() {
        insertRow(1, "2012-01-01", "2012-01-02");
        assertThat(countBugs("2012-01-03", "2012-01-04"), is(0));
    }

    @Test
    public void should_return_one_when_bug_start_date_equals_to_range_end_date() {
        insertRow(1, "2012-01-02", "2012-01-03");
        assertThat(countBugs("2012-01-01", "2012-01-02"), is(1));
    }

    @Test
    public void should_return_one_when_bug_end_date_equals_to_range_start_date() {
        insertRow(1, "2012-01-01", "2012-01-02");
        assertThat(countBugs("2012-01-02", "2012-01-03"), is(1));
    }

    @Test
    public void should_return_one_when_bug_start_and_end_dates_less_and_greater_than_range() {
        insertRow(1, "2012-01-01", "2012-01-04");
        assertThat(countBugs("2012-01-02", "2012-01-03"), is(1));
    }

    @Test
    public void should_return_one_when_bug_start_and_end_dates_equals_to_range() {
        insertRow(1, "2012-01-01", "2012-01-02");
        assertThat(countBugs("2012-01-01", "2012-01-02"), is(1));
    }

    @Test
    public void should_return_two_when_two_bugs_dates_intersects_with_range() {
        insertRow(1, "2012-01-01", "2012-01-05");
        insertRow(2, "2012-01-02", "2012-01-04");
        assertThat(countBugs("2012-01-03", "2012-01-03"), is(2));
    }

    @BeforeClass
    public static void initDatabase() throws IOException {
        Properties properties = new Properties();
        properties.load(CountBugsProcedureTest.class.getResourceAsStream("/database.properties"));
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser(properties.getProperty("username"));
        ds.setPassword(properties.getProperty("password"));
        ds.setURL(properties.getProperty("url"));
        DBI dbi = new DBI(ds);
        handle = dbi.open();
        handle.execute("CREATE TABLE bugs(id INT, open_date DATE, close_date DATE, severity INT)");
        String query = new String(readAllBytes(get("src/main/sql/CountBugsProcedure.sql")));
        Batch batch = handle.createBatch();
        compile(quote("$$")).splitAsStream(query).filter(s -> !s.contains("DELIMITER")).forEach(batch::add);
        batch.execute();
    }

    @AfterClass
    public static void closeDatabase() {
        handle.execute("DROP TABLE bugs");
        handle.close();
    }

    @Before
    public void setUp() {
        handle.execute("TRUNCATE TABLE bugs");
    }

    private void insertRow(int id, String dateFrom, String dateTo) {
        handle.insert("INSERT INTO bugs VALUES (?,?,?,?)", id, dateFrom, dateTo, id);
    }

    private int countBugs(String dateFrom, String dateTo) {
        return handle.createQuery("call countBugs(?,?)")
                .bind(0, dateFrom).bind(1, dateTo)
                .map(PRIMITIVE).first();
    }
}
