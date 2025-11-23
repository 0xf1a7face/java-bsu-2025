package lab2;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a transaction in the system.
 */
public class Transaction {
    private final UUID uuid;
    private final Instant timestamp;
    private final Action action;

    public Transaction(UUID uuid, Instant timestamp, Action action) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.action = action;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Action getAction() {
        return action;
    }

    public boolean execute() {
        if (action.execute()) {
            TransactionObserver.getInstance().notify(this);
            return true;
        }
        return false;
    }

    public void execute_throw() {
        if (!this.execute()) {
            throw new RuntimeException("Failed to execute transaction : " + this.toString());
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "uuid=" + (uuid != null ? uuid : "null") +
                ", timestamp=" + (timestamp != null ? timestamp : "null") +
                ", action=" + (action != null ? action : "null") +
                '}';
    }
}
