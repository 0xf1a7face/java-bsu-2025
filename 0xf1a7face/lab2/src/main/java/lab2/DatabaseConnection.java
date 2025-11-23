package lab2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Singleton database connection.
 */
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final Connection connection;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:mem:transaction_db;DB_CLOSE_DELAY=-1");
            initializeSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public ReentrantReadWriteLock.ReadLock getReadLock() {
        return lock.readLock();
    }

    public ReentrantReadWriteLock.WriteLock getWriteLock() {
        return lock.writeLock();
    }

    private void initializeSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        uuid VARCHAR(36) PRIMARY KEY,
                        amount DECIMAL(19,2) NOT NULL,
                        frozen BOOLEAN NOT NULL DEFAULT FALSE,
                        comment TEXT DEFAULT NULL
                    )
                    """);
        }
    }
}
