package lab2;

import java.time.Instant;
import java.util.UUID;

/**
 * Builder for creating transactions.
 */
public class TransactionBuilder {
    private UUID uuid = UUID.randomUUID();
    private Instant timestamp = Instant.now();

    public TransactionBuilder withUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public TransactionBuilder withTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Transaction createAccount(UUID accountUuid) {
        return this.build(new CreateAccountAction(accountUuid, new Amount(0.0)));
    }

    public Transaction createAccountWithComment(UUID accountUuid, String comment) {
        return this.build(new CreateAccountAction(accountUuid, new Amount(0.0), comment));
    }

    public Transaction deleteAccount(UUID accountUuid) {
        return this.build(new DeleteAccountAction(accountUuid));
    }

    public Transaction withdraw(UUID accountUuid, Amount amount) {
        return this.build(new WithdrawAction(accountUuid, amount));
    }

    public Transaction deposit(UUID accountUuid, Amount amount) {
        return this.build(new DepositAction(accountUuid, amount));
    }

    public Transaction transfer(UUID fromAccount, UUID toAccount, Amount amount) {
        return this.build(new TransferAction(fromAccount, toAccount, amount));
    }

    public Transaction freeze(UUID accountUuid) {
        return this.build(new FreezeAction(accountUuid));
    }

    public Transaction unfreeze(UUID accountUuid) {
        return this.build(new UnfreezeAction(accountUuid));
    }

    Transaction build(Action action) {
        if (action == null) {
            throw new IllegalStateException("Action is null");
        }
        return new Transaction(uuid, timestamp, action);
    }
}
