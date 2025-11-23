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
 * Freezes an account (prevents transactions).
 */
public class FreezeAction implements Action {
    private final UUID accountUuid;

    public FreezeAction(UUID accountUuid) {
        this.accountUuid = accountUuid;
    }

    @Override
    public boolean execute() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.getWriteLock().lock();
        try {
            String sql = "UPDATE accounts SET frozen = TRUE WHERE uuid = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
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
