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
 * Represents a user in the system.
 */
public class User {
    private final UUID uuid;
    private String name;
    private final List<UUID> accounts;

    public User(UUID uuid, String name, List<UUID> accounts) {
        this.uuid = uuid;
        this.name = name;
        this.accounts = new ArrayList<>(accounts);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UUID> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void addAccount(UUID accountUuid) {
        accounts.add(accountUuid);
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
