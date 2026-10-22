package exception;

public class DailyLimitExceededException extends BankException {
    public DailyLimitExceededException(long limit) { super("Daily withdrawal limit of " + limit + " exceeded"); }
}
