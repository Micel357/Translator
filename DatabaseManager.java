package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:translator.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    public static void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTables() {
        String sqlTranslations = "CREATE TABLE IF NOT EXISTS translations (\n" +
                                 "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                 "    source_text TEXT NOT NULL,\n" +
                                 "    source_lang TEXT NOT NULL,\n" +
                                 "    target_text TEXT NOT NULL,\n" +
                                 "    target_lang TEXT NOT NULL,\n" +
                                 "    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP\n" +
                                 ");";

        String sqlLanguageProfiles = "CREATE TABLE IF NOT EXISTS language_profiles (\n" +
                                     "    lang_code TEXT PRIMARY KEY,\n" +
                                     "    char_frequencies TEXT NOT NULL\n" +
                                     ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlTranslations);
            stmt.execute(sqlLanguageProfiles);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}


