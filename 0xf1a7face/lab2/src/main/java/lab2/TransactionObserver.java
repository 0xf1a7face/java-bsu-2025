package lab2;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class TransactionObserver {
    private static volatile TransactionObserver instance;
    private final Set<TransactionListener> listeners = new CopyOnWriteArraySet<>();

    private TransactionObserver() {
    }

    public static TransactionObserver getInstance() {
        if (instance == null) {
            synchronized (TransactionObserver.class) {
                if (instance == null) {
                    instance = new TransactionObserver();
                }
            }
        }
        return instance;
    }

    public void addListener(TransactionListener listener) {

        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListener(TransactionListener listener) {
        listeners.remove(listener);
    }

    public void notify(Transaction transaction) {
        if (transaction == null) {
            return;
        }

        synchronized (this) {
            for (TransactionListener listener : listeners) {
                try {
                    listener.onTransaction(transaction);
                } catch (Exception e) {
                    System.err.println("Error in TransactionListener: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
