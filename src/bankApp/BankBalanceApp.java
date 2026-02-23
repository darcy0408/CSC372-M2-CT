package bankApp;/*
//// Final Version with Opening Balance
 * bankApp.BankBalanceApp.java
 * CSC372 - Module 2 Critical Thinking Assignment
 *
 * GUI Bank Balance Application.
 * Obtains account details and opening balance from the user,
 * then supports deposit and withdrawal with live balance display.
 *
 * Design Decisions:
 * 1. All fields validated before touching bankApp.BankAccount (prevents null display).
 * 2. Opening balance entered by user during account setup via deposit().
 * 3. Deposit/Withdraw disabled until account setup succeeds.
 * 4. WindowAdapter ensures final balance dialog on X-button exit too.
 * 5. Helper methods keep actionPerformed() clean (Programming Style).
 * 6. NumberFormat for locale-aware currency display.
 *
 * Author: Darcy Van Pelt
 * Date: February 2026
 */
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import javax.swing.*;

public class BankBalanceApp extends JFrame implements ActionListener {

    // Business Logic
    private final BankAccount account = new BankAccount();
    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance();

    // GUI Components
    private final JTextField firstNameField      = new JTextField(10);
    private final JTextField lastNameField       = new JTextField(10);
    private final JTextField accountIDField      = new JTextField(10);
    private final JTextField openingBalanceField = new JTextField(10);
    private final JTextField amountField         = new JTextField(10);
    private final JLabel     balanceLabel        =
            new JLabel("Current Balance: $0.00");
    private final JLabel     messageLabel        =
            new JLabel("Please set up your account to begin.");
    private final JButton    setupButton    = new JButton("Set Up Account");
    private final JButton    depositButton  = new JButton("Deposit");
    private final JButton    withdrawButton = new JButton("Withdraw");
    private final JButton    exitButton     = new JButton("Exit");

    public BankBalanceApp() {
        super("Bank Balance Application");

        // Disable transactions until account is initialized
        depositButton.setEnabled(false);
        withdrawButton.setEnabled(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: First Name
        addFormField(panel, "First Name:", firstNameField, 0, gbc);
        // Row 1: Last Name
        addFormField(panel, "Last Name:", lastNameField, 1, gbc);

        // Row 2: Account ID + Setup button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Account ID:"), gbc);
        gbc.gridx = 1; panel.add(accountIDField, gbc);
        gbc.gridx = 2; panel.add(setupButton, gbc);

        // Row 3: Opening Balance (entered once at setup)
        addFormField(panel, "Opening Balance:", openingBalanceField, 3, gbc);

        // Row 4: Transaction Amount
        addFormField(panel, "Amount:", amountField, 4, gbc);

        // Row 5: Transaction buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panel.add(depositButton, gbc);
        gbc.gridx = 1; panel.add(withdrawButton, gbc);

        // Row 6: Balance display (bold, centered)
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        balanceLabel.setHorizontalAlignment(JLabel.CENTER);
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(balanceLabel, gbc);

        // Row 7: Status message (blue)
        gbc.gridy = 7;
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setForeground(Color.BLUE);
        panel.add(messageLabel, gbc);

        // Row 8: Exit button
        gbc.gridy = 8; panel.add(exitButton, gbc);

        // Register action listeners
        setupButton.addActionListener(this);
        depositButton.addActionListener(this);
        withdrawButton.addActionListener(this);
        exitButton.addActionListener(this);

        // X button also shows final balance dialog
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { handleExit(); }
        });

        add(panel);
        setSize(580, 440);
        setLocationRelativeTo(null);
    }

    /** Helper: adds a label + text field pair to the GridBag panel. */
    private void addFormField(JPanel p, String label,
                              JTextField field, int row,
                              GridBagConstraints g) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(new JLabel(label), g);
        g.gridx = 1; p.add(field, g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if      (e.getSource() == setupButton)    handleSetup();
        else if (e.getSource() == depositButton)  handleDeposit();
        else if (e.getSource() == withdrawButton) handleWithdraw();
        else if (e.getSource() == exitButton)     handleExit();
    }

    // ─── Logic Helper Methods ────────────────────────────────────────────────

    /**
     * Validates all fields, sets up the account, and applies the
     * user-provided opening balance via deposit() so bankApp.BankAccount's
     * balance logic remains unmodified.
     */
    private void handleSetup() {
        String  fName   = firstNameField.getText().trim();
        String  lName   = lastNameField.getText().trim();
        Integer id      = parseID(accountIDField.getText());
        Double  opening = parseAmount(openingBalanceField.getText());

        if (fName.isEmpty() || lName.isEmpty()
                || id == null || opening == null || opening < 0) {
            messageLabel.setText(
                    "Error: All fields required. ID must be positive. " +
                            "Opening balance must be 0 or greater.");
            return;
        }
        account.setFirstName(fName);
        account.setLastName(lName);
        account.setAccountID(id);
        if (opening > 0) account.deposit(opening);

        depositButton.setEnabled(true);
        withdrawButton.setEnabled(true);
        openingBalanceField.setEnabled(false); // lock after setup
        messageLabel.setText("Account active for: " + fName + " " + lName);
        updateDisplay();
    }

    private void handleDeposit() {
        Double val = parseAmount(amountField.getText());
        if (val != null && val > 0) {
            account.deposit(val);
            messageLabel.setText("Successfully deposited "
                    + currencyFormat.format(val));
            amountField.setText("");
            updateDisplay();
        } else {
            messageLabel.setText("Error: Enter a valid positive amount.");
        }
    }

    private void handleWithdraw() {
        Double val = parseAmount(amountField.getText());
        if (val != null && val > 0) {
            double before = account.getBalance();
            account.withdrawal(val);
            if (account.getBalance() < before) {
                messageLabel.setText("Successfully withdrew "
                        + currencyFormat.format(val));
                amountField.setText("");
            } else {
                messageLabel.setText("Error: Insufficient funds.");
            }
            updateDisplay();
        } else {
            messageLabel.setText("Error: Enter a valid positive amount.");
        }
    }

    private void handleExit() {
        String summary = String.format(
                "Final Summary%nAccount Holder: %s %s%n" +
                        "Account ID: %d%nFinal Balance: %s",
                account.getFirstName(), account.getLastName(),
                account.getAccountID(),
                currencyFormat.format(account.getBalance()));
        JOptionPane.showMessageDialog(this, summary,
                "Session End", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        System.exit(0);
    }

    private void updateDisplay() {
        balanceLabel.setText("Current Balance: "
                + currencyFormat.format(account.getBalance()));
    }

    private Double parseAmount(String t) {
        try { return Double.parseDouble(t.trim()); }
        catch (Exception e) { return null; }
    }

    private Integer parseID(String t) {
        try {
            int id = Integer.parseInt(t.trim());
            return id > 0 ? id : null;
        } catch (Exception e) { return null; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new BankBalanceApp().setVisible(true));
    }
}
