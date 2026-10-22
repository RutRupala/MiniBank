import exception.BankException;
import model.Account;
import service.BankService;
import util.Validator;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MiniBank {
    record BankInfo(String name, String branch) { }

    enum MenuOption {
        OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, STATEMENT, LIST_ACCOUNTS,
        SEARCH_BY_OWNER, BATCH_PROCESS, REPORT, EXIT
    }

    private static final Scanner SC = new Scanner(System.in);

    private static String ask(String prompt) {
        System.out.print(prompt);
        return SC.hasNextLine() ? SC.nextLine().trim() : "";
    }

    private static long askAmount(String prompt) throws BankException {
        String s = ask(prompt);
        if (!Validator.isValidAmount(s)) throw new BankException("Amount must be a positive whole number");
        return Long.parseLong(s);
    }

    public static void main(String[] args) {
        BankService bank = new BankService(Paths.get("data"));
        bank.load();                                           // restore saved accounts
        System.out.println("==== " + new BankInfo("MiniBank", "Charusat Campus Branch") + " ====");

        MenuOption[] options = MenuOption.values();
        boolean running = true;
        while (running) {
            System.out.println("\nMenu:");
            for (int i = 0; i < options.length; i++) System.out.println((i + 1) + ". " + options[i]);
            String input = ask("Choose an option: ");
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number between 1 and " + options.length);
                continue;
            }
            if (choice < 1 || choice > options.length) {
                System.out.println("Invalid option.");
                continue;
            }
            MenuOption selected = options[choice - 1];
            try {
                switch (selected) {
                    case OPEN_ACCOUNT -> {
                        String owner = ask("Owner name: ");
                        String type = ask("Type (savings/current/fd): ");
                        long opening = Long.parseLong(ask("Opening balance: "));
                        Account a = bank.openAccount(owner, type, opening);
                        System.out.println("Opened " + a);
                    }
                    case DEPOSIT -> {
                        String acc = ask("Account number: ");
                        bank.deposit(acc, askAmount("Amount: "));
                        System.out.println("Deposit successful.");
                    }
                    case WITHDRAW -> {
                        String acc = ask("Account number: ");
                        bank.withdraw(acc, askAmount("Amount: "));
                        System.out.println("Withdrawal successful.");
                    }
                    case TRANSFER -> {
                        String from = ask("From account: ");
                        String to = ask("To account: ");
                        bank.transfer(from, to, askAmount("Amount: "));
                    }
                    case STATEMENT -> System.out.println(bank.statement(ask("Account number: ")));
                    case LIST_ACCOUNTS -> {
                        List<Account> all = bank.listAccounts();
                        if (all.isEmpty()) System.out.println("No accounts yet.");
                        all.forEach(a -> System.out.println("  " + a));
                    }
                    case SEARCH_BY_OWNER -> {
                        List<Account> found = bank.searchByOwner(ask("Owner name (or part): "));
                        if (found.isEmpty()) System.out.println("No matching accounts.");
                        found.forEach(a -> System.out.println("  " + a));
                    }
                    case BATCH_PROCESS -> {
                        System.out.println("Enter commands like 'DEPOSIT AC0001 500', one per line; blank line to run:");
                        List<String> lines = new ArrayList<>();
                        String line;
                        while (!(line = ask("> ")).isEmpty()) lines.add(line);
                        System.out.println(bank.runBatch(lines));
                    }
                    case REPORT -> System.out.println(bank.report());
                    case EXIT -> running = false;
                }
            } catch (BankException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid number");
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
        bank.save();                                           // persist on exit
        System.out.println("Data saved. Thank you for using MiniBank. Goodbye!");
    }
}
