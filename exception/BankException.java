package exception;

/** Checked base class for every MiniBank error. */
public class BankException extends Exception {
    public BankException(String message) { super(message); }
}
