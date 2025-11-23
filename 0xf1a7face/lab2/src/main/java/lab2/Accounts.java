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

public class Accounts {
    /**
     * Retrieves an account by UUID from the database.
     * 
     * @param uuid the account UUID
     * @return Account object, or null if not found
     */
    public static Account getAccount(UUID uuid) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.getReadLock().lock();
        try {
            String sql = "SELECT amount, frozen, comment FROM accounts WHERE uuid = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Amount amount = new Amount(rs.getBigDecimal("amount"));
                        boolean frozen = rs.getBoolean("frozen");
                        String comment = rs.getString("comment"); // may be null
                        return new Account(uuid, amount, frozen, comment);
                    }
                }
                return null; // Not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.getReadLock().unlock();
        }
    }

    /**
     * Retrieves all accounts (use cautiously in production).
     */
    public static List<Account> getAllAccounts() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.getReadLock().lock();
        try {
            List<Account> accounts = new ArrayList<>();
            String sql = "SELECT uuid, amount, frozen, comment FROM accounts";
            try (Statement stmt = db.getConnection().createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    Amount amount = new Amount(rs.getBigDecimal("amount"));
                    boolean frozen = rs.getBoolean("frozen");
                    String comment = rs.getString("comment");
                    accounts.add(new Account(uuid, amount, frozen, comment));
                }
            }
            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            db.getReadLock().unlock();
        }
    }
}
