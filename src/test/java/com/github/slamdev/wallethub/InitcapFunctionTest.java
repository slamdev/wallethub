package com.github.slamdev.wallethub;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.StringColumnMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class InitcapFunctionTest {

    private static Handle handle;

    @Test
    public void should_return_same_string_when_it_is_capitalized() {
        assertThat(initcap("Hello"), is("Hello"));
    }

    @Test
    public void should_return_same_string_when_it_starts_not_from_letter() {
        assertThat(initcap("1hello"), is("1hello"));
    }

    @Test
    public void should_return_string_with_capitalized_first_letter() {
        assertThat(initcap("hello"), is("Hello"));
    }

    @Test
    public void should_return_string_with_capitalized_first_letter_of_all_words() {
        assertThat(initcap("hello hello"), is("Hello Hello"));
    }

    @Test
    public void should_return_string_where_all_non_first_letters_lowercased() {
        assertThat(initcap("1Hello HEllo HELLO"), is("1hello Hello Hello"));
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
        String query = new String(readAllBytes(get("src/main/sql/InitcapFunction.sql")));
        Batch batch = handle.createBatch();
        compile(quote("$$")).splitAsStream(query).filter(s -> !s.contains("DELIMITER")).forEach(batch::add);
        batch.execute();
    }

    private String initcap(String string) {
        return handle.createQuery("SELECT initcap(?)").bind(0, string).map(StringColumnMapper.INSTANCE).first();
    }
}
