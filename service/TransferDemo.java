package service;

import exception.BankException;
import model.Account;

public class TransferDemo {
    private static void pause() { try { Thread.sleep(200); } catch (InterruptedException e) { } }

    /** Locks 'from' then 'to' - two opposite transfers can deadlock. */
    public static void riskyTransfer(Account from, Account to, long amount) {
        synchronized (from) {
            pause();
            synchronized (to) {
                try { from.withdraw(amount); to.deposit(amount); } catch (BankException e) { System.out.println(e.getMessage()); }
            }
        }
    }

    /** Always locks the account with the smaller accountNumber first - no deadlock. */
    public static void safeTransfer(Account from, Account to, long amount) {
        Account first = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? from : to;
        Account second = (first == from) ? to : from;
        synchronized (first) {
            pause();
            synchronized (second) {
                try { from.withdraw(amount); to.deposit(amount); } catch (BankException e) { System.out.println(e.getMessage()); }
            }
        }
    }

    /** Runs A->B and B->A at the same time; returns true if the threads got stuck. */
    public static boolean run(Account a, Account b, boolean safe) throws InterruptedException {
        Thread t1 = new Thread(() -> { if (safe) safeTransfer(a, b, 100); else riskyTransfer(a, b, 100); }, "A->B");
        Thread t2 = new Thread(() -> { if (safe) safeTransfer(b, a, 50); else riskyTransfer(b, a, 50); }, "B->A");
        t1.setDaemon(true); t2.setDaemon(true);
        long start = System.currentTimeMillis();
        t1.start(); t2.start();
        t1.join(2000); t2.join(2000);
        long took = System.currentTimeMillis() - start;
        if (took > 1500) System.out.println("WARNING: transfers took " + took + " ms (long-running)");
        return t1.isAlive() || t2.isAlive();
    }
}
