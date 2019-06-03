package com.blecua84.moneytransfers.services.models;

public class Account {
    private String sortCode;
    private String accountNumber;
    private float available;

    public Account(String sortCode, String accountNumber, float available) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.available = available;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public float getAvailable() {
        return available;
    }

    public void setAvailable(float available) {
        this.available = available;
    }
}
