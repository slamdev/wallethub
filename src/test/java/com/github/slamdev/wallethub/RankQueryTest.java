package com.github.slamdev.wallethub;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RankQueryTest {

    private static Handle handle;

    private static String query;

    @Test
    public void should_return_rank_eq_one_for_single_entry() {
        insertRow("a", 0);
        assertThat(ranks(), hasItem(withRankAndName(1, "a")));
    }

    @Test
    public void should_return_greater_rank_for_entry_with_greater_votes() {
        insertRow("a", 1);
        insertRow("b", 10);
        assertThat(ranks(), hasItem(withRankAndName(1, "a")));
        assertThat(ranks(), hasItem(withRankAndName(2, "b")));
    }

    @Test
    public void should_return_same_rank_for_same_votes() {
        insertRow("a", 1);
        insertRow("b", 1);
        assertThat(ranks(), hasItem(withRankAndName(1, "a")));
        assertThat(ranks(), hasItem(withRankAndName(1, "b")));
    }

    @Test
    public void should_return_items_ordered_by_rank() {
        insertRow("a", 10);
        insertRow("b", 30);
        insertRow("c", 1);
        assertThat(ranks().get(0), withRankAndName(1, "c"));
        assertThat(ranks().get(1), withRankAndName(2, "a"));
        assertThat(ranks().get(2), withRankAndName(3, "b"));
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
        handle.execute("CREATE TABLE votes(name CHAR(10), votes INT)");
        query = new String(readAllBytes(get("src/main/sql/RankQuery.sql")));
    }

    @AfterClass
    public static void closeDatabase() {
        handle.execute("DROP TABLE votes");
        handle.close();
    }

    @Before
    public void setUp() {
        handle.execute("TRUNCATE TABLE votes");
    }

    private void insertRow(String name, int votes) {
        handle.insert("INSERT INTO votes VALUES (?,?)", name, votes);
    }

    private Matcher<Object> withRankAndName(int rank, String name) {
        return both(hasProperty("rank", equalTo(rank))).and(hasProperty("name", equalTo(name)));
    }

    private List<Person> ranks() {
        return handle.createQuery(query).map(Person.class).list();
    }

    public static final class Person {
        private String name;
        private int votes;
        private int rank;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }
}
