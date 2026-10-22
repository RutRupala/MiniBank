package model;

public class FixedDepositAccount extends Account implements Premium {
    private static final long serialVersionUID = 1L;

    public FixedDepositAccount(String ownerName, long amount) { super(ownerName, amount); }

    @Override
    public double interestRate() { return 7.0; }
    @Override
    public boolean canWithdraw(long amount) { return false; }   // deposit is locked
    @Override
    public long availableFunds() { return 0; }
}
