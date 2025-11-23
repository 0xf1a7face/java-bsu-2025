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
 * Deposits money into an account.
 */
public class DepositAction implements Action {
    private final UUID accountUuid;
    private final Amount amount;

    public DepositAction(UUID accountUuid, Amount amount) {
        if (amount.isNegative()) {
            throw new IllegalStateException("Amount is negative: " + amount);
        }
        this.accountUuid = accountUuid;
        this.amount = amount;
    }

    @Override
    public boolean execute() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.getWriteLock().lock();
        try {
            String sql = "SELECT frozen FROM accounts WHERE uuid = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, accountUuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return false; // Account doesn't exist
                    }
                    boolean frozen = rs.getBoolean("frozen");
                    if (frozen) {
                        return false; // Account is frozen
                    }
                }
            }

            String updateSql = "UPDATE accounts SET amount = amount + ? WHERE uuid = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(updateSql)) {
                stmt.setBigDecimal(1, amount.getValue());
                stmt.setString(2, accountUuid.toString());
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
