# MiniBank (Practical 14 - final)

Console banking application for OOP (CEUE203). Layers: `MiniBank` (menu/UI) -> `service.BankService` -> `service.Repository<Account>` -> `model.*`.

## Build
Requires JDK 17+.

    javac -d . MiniBank.java $(find model util service exception -name '*.java')
    jar cfm minibank.jar manifest.txt MiniBank*.class model util service exception

## Run
    java -jar minibank.jar

(or `java MiniBank` after compiling). Accounts are saved to `data/accounts.dat` on EXIT and loaded on start-up.
Transactions are appended to `data/logs/bank.log`; menu option 9 builds the end-of-day report in `data/report.txt`.
Do not commit the `data/` folder.

## Menu
1 Open account - 2 Deposit - 3 Withdraw - 4 Transfer - 5 Statement - 6 List accounts - 7 Search by owner -
8 Batch process (thread pool over ConcurrentHashMap) - 9 Report - 10 Exit

Batch input example (blank line runs it): `DEPOSIT AC0001 500`, `WITHDRAW AC0002 200`.

## Tests
* `tests/BankServiceTest.java` - JUnit 5 tests (needs the JUnit 5 library, e.g. in VS Code/IntelliJ/Codespaces).
* `tests/SelfTest.java` - the same checks without any library:

      cd tests && javac -cp .. -d . SelfTest.java && java -cp ..:. SelfTest

## Notes
* Optional @Audit annotation + reflective logger (step 5) was not implemented.
