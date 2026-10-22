package model;

public interface InterestBearing {
    double interestRate();
    long getBalance();

    default double yearlyInterest() { return getBalance() * interestRate() / 100.0; }

    /** Second default method (supplementary). */
    default double quarterlyInterest() { return yearlyInterest() / 4.0; }
}
