package exception;

public class InvalidAmountException extends BankException {
    public InvalidAmountException(long amount) { super("Invalid amount: " + amount + " (must be > 0)"); }
}
