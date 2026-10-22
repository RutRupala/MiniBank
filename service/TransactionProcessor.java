package service;

import java.util.Map;
import model.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {
    private final ExecutorService pool = Executors.newFixedThreadPool(4);
    /** Shared accounts - ConcurrentHashMap is safe for many threads. */
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, Integer> tasksPerThread = new ConcurrentHashMap<>();

    public void register(Account account) { accounts.put(account.getAccountNumber(), account); }

    public Account getAccount(String accountNumber) { return accounts.get(accountNumber); }

    public Map<String, Account> getAccounts() { return accounts; }

    public void submit(Runnable task) {
        pool.execute(() -> {
            tasksPerThread.merge(Thread.currentThread().getName(), 1, Integer::sum);
            task.run();
        });
    }

    public void stop() throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) pool.shutdownNow();
    }

    public Map<String, Integer> getTasksPerThread() { return tasksPerThread; }
}
