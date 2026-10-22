// Same checks as BankServiceTest.java, but runnable WITHOUT the JUnit library (plain Java).
import exception.BankException;
import exception.InsufficientFundsException;
import model.Account;
import service.BankService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SelfTest {
    static int failed = 0;

    static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS " : "FAIL ") + name);
        if (!ok) failed++;
    }

    public static void main(String[] args) throws Exception {
        Path tmp = Files.createTempDirectory("minibank-test");

        BankService bank = new BankService(tmp);
        Account a = bank.openAccount("Riya", "savings", 1000);
        bank.deposit(a.getAccountNumber(), 500);
        check("depositIncreasesBalance", a.getBalance() == 1500);

        Account b = bank.openAccount("Arjun", "savings", 1000);
        boolean threw = false; long shortfall = 0;
        try { bank.withdraw(b.getAccountNumber(), 5000); }
        catch (InsufficientFundsException e) { threw = true; shortfall = e.getShortfall(); }
        check("withdrawalOverLimitThrows", threw && shortfall == 4000 && b.getBalance() == 1000);

        Account c = bank.openAccount("Meera", "savings", 0);
        List<String> batch = new ArrayList<>();
        for (int i = 0; i < 1000; i++) batch.add("DEPOSIT " + c.getAccountNumber() + " 1");
        bank.runBatch(batch);
        check("concurrentDepositsKeepBalanceCorrect", c.getBalance() == 1000);

        bank.save();
        BankService again = new BankService(tmp);
        again.load();
        check("saveAndLoadKeepsData", again.find(c.getAccountNumber()).getBalance() == 1000
                && again.find(a.getAccountNumber()).getBalance() == 1500);

        boolean threw2 = false;
        try { bank.deposit("AC9999", 10); } catch (BankException e) { threw2 = true; }
        check("unknownAccountThrows", threw2);

        System.out.println(failed == 0 ? "\nAll tests passed." : "\n" + failed + " test(s) FAILED.");
        if (failed > 0) System.exit(1);
    }
}
