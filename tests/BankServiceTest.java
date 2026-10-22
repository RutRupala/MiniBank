// JUnit 5 tests for MiniBank (Practical 14).
// Run from VS Code / IntelliJ / Codespaces with JUnit 5 on the classpath, e.g. with the console launcher:
//   javac -cp .:junit-platform-console-standalone.jar -d out $(find . -name '*.java')
//   java -jar junit-platform-console-standalone.jar --class-path out --scan-class-path
import exception.BankException;
import exception.InsufficientFundsException;
import model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.BankService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    @TempDir
    Path tmp;

    @Test
    void depositIncreasesBalance() throws Exception {
        BankService bank = new BankService(tmp);
        Account a = bank.openAccount("Riya", "savings", 1000);
        bank.deposit(a.getAccountNumber(), 500);
        assertEquals(1500, a.getBalance());
    }

    @Test
    void withdrawalOverLimitThrows() throws Exception {
        BankService bank = new BankService(tmp);
        Account a = bank.openAccount("Riya", "savings", 1000);
        InsufficientFundsException e = assertThrows(InsufficientFundsException.class,
                () -> bank.withdraw(a.getAccountNumber(), 5000));
        assertEquals(4000, e.getShortfall());
        assertEquals(1000, a.getBalance());
    }

    @Test
    void concurrentDepositsKeepBalanceCorrect() throws Exception {
        BankService bank = new BankService(tmp);
        Account a = bank.openAccount("Riya", "savings", 0);
        List<String> batch = new ArrayList<>();
        for (int i = 0; i < 1000; i++) batch.add("DEPOSIT " + a.getAccountNumber() + " 1");
        bank.runBatch(batch);
        assertEquals(1000, a.getBalance());
    }

    @Test
    void saveAndLoadKeepsData() throws Exception {
        BankService bank = new BankService(tmp);
        Account a = bank.openAccount("Riya", "current", 2500);
        bank.save();
        BankService again = new BankService(tmp);
        again.load();
        assertEquals(2500, again.find(a.getAccountNumber()).getBalance());
    }

    @Test
    void unknownAccountThrows() {
        BankService bank = new BankService(tmp);
        assertThrows(BankException.class, () -> bank.deposit("AC9999", 10));
    }
}
