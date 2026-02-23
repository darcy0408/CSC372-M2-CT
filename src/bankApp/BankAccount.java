package bankApp;

/*
 * bankApp.BankAccount.java
 * CSC372 - Module 1 Critical Thinking Assignment
 * Reused as the business logic layer for Module 2.
 *
 * Author: Darcy Van Pelt
 * Date: February 2026
 */
public class BankAccount {
    private String firstName;
    private String lastName;
    private int accountID;
    protected double balance;

    public BankAccount() {
        this.balance = 0.0;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println(
                    "Deposit rejected: amount must be greater than 0.");
            return;
        }
        balance += amount;
        System.out.printf("Deposit accepted: $%.2f%n", amount);
    }

    public void withdrawal(double amount) {
        if (amount <= 0) {
            System.out.println(
                    "Withdrawal rejected: amount must be greater than 0.");
            return;
        }
        if (amount > balance) {
            System.out.printf(
                    "Withdrawal rejected: insufficient funds. " +
                            "Requested $%.2f, available $%.2f%n",
                    amount, balance);
            return;
        }
        balance -= amount;
        System.out.printf("Withdrawal accepted: $%.2f%n", amount);
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public int    getAccountID() { return accountID; }
    public double getBalance()   { return balance; }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            System.out.println("First name not set: value was empty.");
            return;
        }
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            System.out.println("Last name not set: value was empty.");
            return;
        }
        this.lastName = lastName.trim();
    }

    public void setAccountID(int accountID) {
        if (accountID <= 0) {
            System.out.println(
                    "Account ID not set: must be a positive integer.");
            return;
        }
        this.accountID = accountID;
    }

    public void accountSummary() {
        System.out.println("========================================");
        System.out.println("Account Summary");
        System.out.println("========================================");
        System.out.println("First Name: "
                + (firstName == null ? "(not set)" : firstName));
        System.out.println("Last Name: "
                + (lastName == null ? "(not set)" : lastName));
        System.out.println("Account ID: " + accountID);
        System.out.printf("Balance: $%.2f%n", balance);
        System.out.println("========================================");
    }
}
