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
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();

    public UserManager() {
    }

    public String createAccount(String name) {
        UUID id = UUID.randomUUID();

        new TransactionBuilder()
                .createAccountWithComment(id, name)
                .execute_throw();
        ;

        List<UUID> accounts = new ArrayList<>();
        accounts.add(id);

        users.put(id.toString(), new User(id, name, accounts));
        return id.toString();
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public BigDecimal getBalance(String accountId) {
        return Accounts.getAccount(UUID.fromString(accountId)).getAmount().getValue();
    }

    public boolean deposit(String accountId, BigDecimal amount) {
        users.computeIfPresent(accountId, (id, acc) -> {
            new TransactionBuilder()
                    .deposit(UUID.fromString(id), new Amount(amount))
                    .execute_throw();
            ;
            return acc;
        });
        return true;
    }

    public boolean withdraw(String accountId, BigDecimal amount) {
        return new TransactionBuilder()
                .withdraw(UUID.fromString(accountId), new Amount(amount))
                .execute();
    }

    public boolean transfer(String fromId, String toId, BigDecimal amount) {
        return new TransactionBuilder()
                .transfer(UUID.fromString(fromId), UUID.fromString(toId), new Amount(amount))
                .execute();
    }

    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }
}
