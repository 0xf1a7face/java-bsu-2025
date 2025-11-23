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

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        TransactionObserver.getInstance().addListener(new TransactionListener() {
            @Override
            public void onTransaction(Transaction transaction) {
                System.out.println("Transaction occured: " + transaction);
            }
        });

        var alice = UUID.randomUUID();
        new TransactionBuilder()
                .createAccountWithComment(alice, "Alice")
                .execute_throw();

        new TransactionBuilder()
                .deposit(alice, new Amount(100.0))
                .execute_throw();

        new TransactionBuilder()
                .withdraw(alice, new Amount(100.0))
                .execute_throw();
        ;

        System.out.println(Accounts.getAllAccounts());

        Gui.main(args);
    }
}
