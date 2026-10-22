package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import util.Command;
import util.CommandParser;
import util.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Business layer: wires Repository, Validator, exceptions, persistence, log and report together. */
public class BankService {
    private final Repository<Account> repo = new Repository<>();
    private final Path dataDir;
    private final Path logFile;

    public BankService(Path dataDir) {
        this.dataDir = dataDir;
        this.logFile = dataDir.resolve("logs").resolve("bank.log");
    }

    // ---------- persistence ----------
    public void load() {
        Path file = dataDir.resolve("accounts.dat");
        if (!Files.exists(file)) return;
        try {
            for (Account a : StatePersister.load(file)) repo.save(a);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load saved accounts: " + e.getMessage());
        }
    }

    public void save() {
        try {
            StatePersister.save(repo.findAll().toArray(new Account[0]), dataDir.resolve("accounts.dat"));
        } catch (IOException e) {
            System.out.println("Could not save accounts: " + e.getMessage());
        }
    }

    // ---------- operations ----------
    public Account openAccount(String owner, String type, long opening) throws BankException {
        if (owner == null || owner.isBlank()) throw new BankException("Owner name must not be blank");
        if (opening < 0) throw new InvalidAmountException(opening);
        Account account = switch (type.toLowerCase()) {
            case "savings" -> new SavingsAccount(owner.trim(), opening, 0);
            case "current" -> new CurrentAccount(owner.trim(), opening, 5000);
            case "fd" -> new FixedDepositAccount(owner.trim(), opening);
            default -> throw new BankException("Unknown account type: " + type + " (use savings/current/fd)");
        };
        repo.save(account);
        return account;
    }

    public Account find(String accountNumber) throws AccountNotFoundException {
        if (!Validator.isValidAccountNumber(accountNumber)) throw new AccountNotFoundException(accountNumber);
        Account a = repo.findById(accountNumber);
        if (a == null) throw new AccountNotFoundException(accountNumber);
        return a;
    }

    public void deposit(String accountNumber, long amount) throws BankException {
        find(accountNumber).deposit(amount);
        log("DEPOSIT " + accountNumber + " " + amount);
    }

    public void withdraw(String accountNumber, long amount) throws BankException {
        find(accountNumber).withdraw(amount);
        log("WITHDRAW " + accountNumber + " " + amount);
    }

    public void transfer(String from, String to, long amount) throws BankException {
        Account a = find(from), b = find(to);
        a.transfer(b, amount);
        log("TRANSFER " + from + " " + to + " " + amount);
    }

    public String statement(String accountNumber) throws BankException {
        return StatementFormatter.buildStatement(find(accountNumber));
    }

    public List<Account> listAccounts() {
        List<Account> list = repo.findAll();
        list.sort(Comparator.naturalOrder());
        return list;
    }

    /** Supplementary: search by owner name (case-insensitive, partial match). */
    public List<Account> searchByOwner(String name) {
        List<Account> result = new ArrayList<>();
        for (Account a : listAccounts()) {
            if (a.getOwnerName().toLowerCase().contains(name.toLowerCase())) result.add(a);
        }
        return result;
    }

    // ---------- batch + report ----------
    /** Runs lines like "DEPOSIT AC0001 500" on a 4-thread pool over a ConcurrentHashMap of accounts. */
    public String runBatch(List<String> lines) throws InterruptedException {
        TransactionProcessor processor = new TransactionProcessor();
        for (Account a : repo.findAll()) processor.register(a);
        java.util.concurrent.atomic.AtomicInteger ok = new java.util.concurrent.atomic.AtomicInteger();
        java.util.concurrent.atomic.AtomicInteger failed = new java.util.concurrent.atomic.AtomicInteger();
        for (String line : lines) {
            processor.submit(() -> {
                try {
                    Command c = CommandParser.parse(line);
                    Account a = processor.getAccount(c.accountNumber());
                    if (a == null) throw new AccountNotFoundException(c.accountNumber());
                    switch (c.type()) {
                        case DEPOSIT -> a.deposit(c.amount());
                        case WITHDRAW -> a.withdraw(c.amount());
                        default -> throw new BankException("TRANSFER is not supported in batch mode");
                    }
                    log(c.type() + " " + c.accountNumber() + " " + c.amount());
                    ok.incrementAndGet();
                } catch (BankException | IllegalArgumentException e) {
                    failed.incrementAndGet();
                    System.out.println("  batch line failed [" + line + "]: " + e.getMessage());
                }
            });
        }
        processor.stop();
        return "Batch finished: " + ok.get() + " succeeded, " + failed.get() + " failed";
    }

    public String report() throws IOException {
        Files.createDirectories(dataDir);
        return ReportGenerator.generate(dataDir.resolve("logs"), dataDir.resolve("report.txt"));
    }

    private synchronized void log(String line) {
        try {
            TransactionLog.append(logFile, line);
        } catch (IOException e) {
            System.out.println("Warning: could not write log: " + e.getMessage());
        }
    }
}
