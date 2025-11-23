package lab2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Map;

public class Gui extends JFrame {

    private final UserManager userManager = new UserManager();

    // UI Components
    private JTable accountTable;
    private DefaultTableModel accountTableModel;
    private JTextArea logArea;
    private JLabel fromAccountLabel;
    private JComboBox<String> fromAccountCombo;
    private JLabel toAccountLabel;
    private JComboBox<String> toAccountCombo;
    private JTextField amountField;
    private JComboBox<String> actionType;

    public Gui() {
        setTitle("Banking Transaction System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);


        userManager.createAccount("Alice");
        userManager.createAccount("Bob");

        initComponents();
        updateAccountLists();
        updateFormVisibility();
        setupTransactionObserver();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel accountPanel = new JPanel(new BorderLayout());
        accountPanel.setBorder(BorderFactory.createTitledBorder("Account Balances"));

        String[] accountColumns = { "Name", "Account", "Balance" };
        accountTableModel = new DefaultTableModel(accountColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountTable = new JTable(accountTableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane accountScroll = new JScrollPane(accountTable);
        accountPanel.add(accountScroll, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create Transaction"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Action:"), gbc);
        actionType = new JComboBox<>(new String[] { "Deposit", "Withdraw", "Transfer" });
        gbc.gridx = 1;
        formPanel.add(actionType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        fromAccountLabel = new JLabel("From Account:");
        formPanel.add(fromAccountLabel, gbc);
        fromAccountCombo = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(fromAccountCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        toAccountLabel = new JLabel("To Account:");
        formPanel.add(toAccountLabel, gbc);
        toAccountCombo = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(toAccountCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Amount ($):"), gbc);
        amountField = new JTextField(10);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        JButton submitBtn = new JButton("Execute Transaction");
        submitBtn.addActionListener(this::executeTransaction);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitBtn, gbc);

        actionType.addActionListener(e -> updateFormVisibility());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Transaction Log"));

        JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, accountPanel, formPanel);
        topSplit.setDividerLocation(400);
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, logScroll);
        mainSplit.setDividerLocation(300);
        mainSplit.setResizeWeight(0.5);

        add(mainSplit, BorderLayout.CENTER);
    }

    private void updateFormVisibility() {
        String action = (String) actionType.getSelectedItem();
        boolean isTransfer = "Transfer".equals(action);
        toAccountCombo.setVisible(isTransfer);
        toAccountLabel.setVisible(isTransfer);
        if (isTransfer) {
            fromAccountLabel.setText("From Account:");
        } else {
            fromAccountLabel.setText("Account:");
        }
        ((GridBagLayout) toAccountCombo.getParent().getLayout()).getConstraints(toAccountCombo).gridy = isTransfer ? 2
                : 3;
        formPanelValidate();
    }

    private void formPanelValidate() {
        SwingUtilities.invokeLater(() -> {
            Container parent = toAccountCombo.getParent();
            parent.revalidate();
            parent.repaint();
        });
    }

    private void updateAccountLists() {
        fromAccountCombo.removeAllItems();
        toAccountCombo.removeAllItems();

        Map<String, User> accounts = userManager.getAllUsers();
        for (User acc : accounts.values()) {
            fromAccountCombo.addItem(acc.getUuid().toString());
            toAccountCombo.addItem(acc.getUuid().toString());
        }

        refreshAccountTable();
    }

    private void refreshAccountTable() {
        accountTableModel.setRowCount(0); // clear
        Map<String, User> accounts = userManager.getAllUsers();
        for (User acc : accounts.values()) {
            // User may have multiple accounts, but for simplicity of the UI we assume it's unique
            accountTableModel
                    .addRow(new Object[] { acc.getName(), acc.getAccounts().get(0).toString(), "$" + userManager.getBalance(acc.getUuid().toString()) });
        }
    }

    private void executeTransaction(ActionEvent e) {
        try {
            String action = (String) actionType.getSelectedItem();
            String fromId = (String) fromAccountCombo.getSelectedItem();
            String toId = (String) toAccountCombo.getSelectedItem();
            BigDecimal amount = new BigDecimal(amountField.getText().trim());

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Transaction transaction = null;
            boolean success = false;

            switch (action) {
                case "Deposit":
                    success = userManager.deposit(fromId, amount);
                    break;
                case "Withdraw":
                    success = userManager.withdraw(fromId, amount);
                    break;
                case "Transfer":
                    success = userManager.transfer(fromId, toId, amount);
                    break;
            }

            if (success) {
                refreshAccountTable();
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Transaction failed (e.g., insufficient funds).", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.", "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setupTransactionObserver() {
        TransactionObserver.getInstance().addListener(transaction -> {
            String log = "[" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_TIME)
                    + "] " + transaction.toString() + "\n";
            SwingUtilities.invokeLater(() -> {
                logArea.append(log);
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Gui().setVisible(true);
        });
    }
}
