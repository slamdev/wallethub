package com.github.slamdev.wallethub;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.hamcrest.Matcher;
import org.junit.*;
import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Ignore
public class SplitProcedureTest {

    private static Handle handle;

    @Test
    public void should_return_no_entries_when_table_is_empty() {
        assertThat(split().size(), is(0));
    }

    @Test
    public void should_return_single_entry_when_row_contains_not_delimiter() {
        insertRow(1, "A");
        assertThat(split(), hasItem(withIdAndName(1, "A")));
    }

    @Test
    public void should_return_single_empty_entry_when_row_contains_only_delimiter() {
        insertRow(1, "|");
        assertThat(split(), hasItem(withIdAndName(1, "")));
    }

    @Test
    public void should_return_single_entry_when_row_ends_with_delimiter() {
        insertRow(1, "A|");
        assertThat(split(), hasItem(withIdAndName(1, "A")));
    }

    @Test
    public void should_return_single_entry_when_row_begins_with_delimiter() {
        insertRow(1, "|A");
        assertThat(split(), hasItem(withIdAndName(1, "A")));
    }

    @Test
    public void should_return_two_entries_when_row_contains_delimiter() {
        insertRow(1, "A|B");
        assertThat(split(), hasItem(withIdAndName(1, "A")));
        assertThat(split(), hasItem(withIdAndName(1, "B")));
    }

    @Test
    public void should_return_two_entries_when_two_row_contains_delimiter() {
        insertRow(1, "A|B");
        insertRow(2, "C|D");
        assertThat(split(), hasItem(withIdAndName(1, "A")));
        assertThat(split(), hasItem(withIdAndName(1, "B")));
        assertThat(split(), hasItem(withIdAndName(2, "C")));
        assertThat(split(), hasItem(withIdAndName(2, "D")));
    }

    @BeforeClass
    public static void initDatabase() throws IOException {
        Properties properties = new Properties();
        try (InputStream is = CountBugsProcedureTest.class.getResourceAsStream("/database.properties")){
            properties.load(is);
        }
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser(properties.getProperty("username"));
        ds.setPassword(properties.getProperty("password"));
        ds.setURL(properties.getProperty("url"));
        DBI dbi = new DBI(ds);
        handle = dbi.open();
        handle.execute("CREATE TABLE names(id INT, name VARCHAR(50))");
        String query = new String(readAllBytes(get("src/main/sql/SplitProcedure.sql")));
        Batch batch = handle.createBatch();
        compile(quote("$$")).splitAsStream(query).filter(s -> !s.contains("DELIMITER")).forEach(batch::add);
        batch.execute();
    }

    @AfterClass
    public static void closeDatabase() {
        handle.execute("DROP TABLE names");
        handle.close();
    }

    @Before
    public void setUp() {
        handle.execute("TRUNCATE TABLE names");
    }

    private Matcher<Object> withIdAndName(int id, String name) {
        return both(hasProperty("id", equalTo(id))).and(hasProperty("name", equalTo(name)));
    }

    private void insertRow(int id, String names) {
        handle.insert("INSERT INTO names VALUES (?,?)", id, names);
    }

    private List<Entry> split() {
        return handle.createQuery("call split()").map(Entry.class).list();
    }

    public static final class Entry {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
