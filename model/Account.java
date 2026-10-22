package model;

import exception.BankException;
import java.io.Serializable;
import exception.DailyLimitExceededException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.MaxLength;
import model.annotation.Positive;

public abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {
    private static final long serialVersionUID = 1L;

    public static final long DAILY_LIMIT = 50000;
    private static long counter = 0;

    @Id
    private final String accountNumber;
    @MaxLength(10)
    private String ownerName;
    @Positive
    private long balance;
    private boolean active;
    private transient long withdrawnToday;   // daily counter is not persisted

    public Account(String ownerName, long openingBalance) {
        counter++;
        this.accountNumber = String.format("AC%04d", counter);
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) { this(ownerName, 0); }

    /** After loading saved accounts, make sure new account numbers do not repeat old ones. */
    public static synchronized void ensureCounterAtLeast(long n) { if (n > counter) counter = n; }

    /** Annual interest rate in percent - each account type decides. */
    public abstract double interestRate();

    /** Each account type decides whether this withdrawal is allowed. */
    public abstract boolean canWithdraw(long amount);

    /** How much can be taken out right now (balance, minus minimum, plus overdraft ...). */
    public long availableFunds() { return balance; }

    public double monthlyInterest() { return balance * interestRate() / 100.0 / 12.0; }

    /** Thread-safe: only one thread at a time may change the balance. */
    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        balance += amount;
    }

    /**
     * DEMONSTRATION ONLY - the original, NOT thread-safe deposit.
     * The read / yield / write sequence lets threads overwrite each other's updates (race condition).
     */
    public void depositUnsafe(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        long current = balance;
        Thread.yield();
        balance = current + amount;
    }

    @Override
    public synchronized void withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException, DailyLimitExceededException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (withdrawnToday + amount > DAILY_LIMIT) throw new DailyLimitExceededException(DAILY_LIMIT);
        if (!canWithdraw(amount)) throw new InsufficientFundsException(amount - availableFunds());
        balance -= amount;
        withdrawnToday += amount;
    }

    /** Moves money to another account; rolls back and re-throws on failure. */
    public void transfer(Account to, long amount) throws BankException {
        try {
            withdraw(amount);
            try {
                to.deposit(amount);
            } catch (BankException e) {
                balance += amount;              // roll back the withdrawal
                withdrawnToday -= amount;
                throw e;
            }
            System.out.println("Transfer of " + amount + " from " + accountNumber + " to " + to.accountNumber + " OK");
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            throw e;                            // rethrow to the caller
        } finally {
            System.out.println("[audit] transfer attempt finished");
        }
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    @Override
    public synchronized long getBalance() { return balance; }
    public boolean isActive() { return active; }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + balance + " | " + (active ? "active" : "inactive");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        return accountNumber.equals(((Account) o).accountNumber);
    }

    @Override
    public int hashCode() { return accountNumber.hashCode(); }

    @Override
    public int compareTo(Account other) { return accountNumber.compareTo(other.accountNumber); }
}
