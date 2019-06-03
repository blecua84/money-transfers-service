package com.blecua84.moneytransfers.router.models;

import java.util.Objects;

public class AccountDTO {
    private String sortCode;
    private String accountNumber;

    public AccountDTO() {
    }

    public AccountDTO(String sortCode, String accountNumber) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountDTO)) return false;
        AccountDTO that = (AccountDTO) o;
        return Objects.equals(getSortCode(), that.getSortCode()) &&
                Objects.equals(getAccountNumber(), that.getAccountNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSortCode(), getAccountNumber());
    }
}
