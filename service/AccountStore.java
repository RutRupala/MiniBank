package service;

import model.Account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AccountStore {
    private final Map<String, Account> accounts = new HashMap<>();     // key = account number
    private final ArrayList<String> history = new ArrayList<>();

    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
        history.add("ADDED " + account.getAccountNumber());
    }

    public Account get(String accountNumber) { return accounts.get(accountNumber); }

    public Collection<Account> getAll() { return accounts.values(); }

    public ArrayList<String> getHistory() { return history; }

    /** Highest balance first. */
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }

    /** Natural order (Comparable): by account number. */
    public ArrayList<Account> sortedByNumber() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        java.util.Collections.sort(list);
        return list;
    }

    /** Supplementary: accounts with balance above a value. */
    public ArrayList<Account> balanceAbove(long value) {
        ArrayList<Account> result = new ArrayList<>();
        for (Account a : accounts.values()) if (a.getBalance() > value) result.add(a);
        return result;
    }

    /** Supplementary: a TreeMap view keeps accounts in sorted key order. */
    public TreeMap<String, Account> asTreeMap() { return new TreeMap<>(accounts); }
}
