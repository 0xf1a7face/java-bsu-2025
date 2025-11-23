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
 * Represents a financial account.
 */
public class Account {
    private final UUID uuid;
    private final Amount amount;
    private final boolean frozen;
    private final String comment;

    public Account(UUID uuid, Amount amount, boolean frozen, String comment) {
        this.uuid = uuid;
        this.amount = amount;
        this.frozen = frozen;
        this.comment = comment;
    }

    public Account(UUID uuid, Amount amount, String comment) {
        this(uuid, amount, false, comment);
    }

    public Account(UUID uuid, Amount amount) {
        this(uuid, amount, false, null);
    }

    public UUID getUuid() { return uuid; }
    public Amount getAmount() { return amount; }
    public boolean isFrozen() { return frozen; }
    public String getComment() { return comment; }

    @Override
    public String toString() {
        return "Account{" +
                "uuid=" + uuid +
                ", amount=" + amount +
                ", frozen=" + frozen +
                ", comment='" + comment + '\'' +
                '}';
    }
}
