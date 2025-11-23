package lab2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class CreateAccountAction implements Action {
    private final UUID accountUuid;
    private final Amount initialAmount;
    private final String comment;

    public CreateAccountAction(UUID accountUuid, Amount initialAmount) {
        this(accountUuid, initialAmount, null);
    }

    public CreateAccountAction(UUID accountUuid, Amount initialAmount, String comment) {
        this.accountUuid = accountUuid;
        this.initialAmount = initialAmount;
        this.comment = comment;
    }

    @Override
    public boolean execute() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.getWriteLock().lock();
        System.out.println("Executing");
        try {
            String sql = "INSERT INTO accounts (uuid, amount, frozen, comment) VALUES (?, ?, FALSE, ?)";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                stmt.setString(1, accountUuid.toString());
                stmt.setBigDecimal(2, initialAmount.getValue());
                stmt.setString(3, comment); // null is allowed
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } finally {
            db.getWriteLock().unlock();
        }
    }
}
