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

public class DeleteAccountAction implements Action {
    private final UUID accountUuid;

    public DeleteAccountAction(UUID accountUuid) {
        this.accountUuid = accountUuid;
    }

    @Override
    public boolean execute() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.getWriteLock().lock();
        try {
            // Check conditions before deletion
            String checkSql = "SELECT amount, frozen FROM accounts WHERE uuid = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(checkSql)) {
                stmt.setString(1, accountUuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return false; // Account doesn't exist
                    }
                    Amount balance = new Amount(rs.getBigDecimal("amount"));
                    boolean frozen = rs.getBoolean("frozen");

                    if (!balance.isZero() || frozen) {
                        return false; // Cannot delete non-zero or frozen account
                    }
                }
            }

            // Safe to delete
            String deleteSql = "DELETE FROM accounts WHERE uuid = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(deleteSql)) {
                stmt.setString(1, accountUuid.toString());
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.getWriteLock().unlock();
        }
    }
}
