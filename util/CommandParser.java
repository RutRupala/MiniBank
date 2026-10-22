package util;

public class CommandParser {
    /** Parses a line such as "DEPOSIT AC0001 500". */
    public static Command parse(String line) {
        if (line == null) throw new IllegalArgumentException("Empty command");
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected 3 parts (TYPE ACCOUNT AMOUNT) but got " + parts.length);
        }
        TransactionType type = TransactionType.valueOf(parts[0].toUpperCase());
        if (!Validator.isValidAmount(parts[2])) throw new IllegalArgumentException("Invalid amount: " + parts[2]);
        return new Command(type, parts[1], Long.parseLong(parts[2]));
    }
}
