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
 * Transfers money between two accounts.
 */
public class TransferAction implements Action {
    private final UUID fromAccountUuid;
    private final UUID toAccountUuid;
    private final Amount amount;

    public TransferAction(UUID fromAccountUuid, UUID toAccountUuid, Amount amount) {
        if (amount.isNegative()) {
            throw new IllegalStateException("Amount is negative: " + amount);
        }
        this.fromAccountUuid = fromAccountUuid;
        this.toAccountUuid = toAccountUuid;
        this.amount = amount;
    }

    @Override
    public boolean execute() {
        WithdrawAction withdraw = new WithdrawAction(fromAccountUuid, amount);
        DepositAction deposit = new DepositAction(toAccountUuid, amount);

        // This should really be wrapped in a db transaction, but nah. We don't even
        // doing logging anyway so whatever.
        if (!withdraw.execute()) {
            return false;
        }

        if (!deposit.execute()) {
            // This shouldn't fail with our backend
            DepositAction rollback = new DepositAction(fromAccountUuid, amount);
            rollback.execute();
            return false;
        }

        return true;
    }
}
