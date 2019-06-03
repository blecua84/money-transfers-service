package com.blecua84.moneytransfers.router.models;

import java.util.Objects;

public class TransferDTO {
    private AccountDTO from;
    private AccountDTO to;
    private String amount;

    public TransferDTO() {
    }

    public TransferDTO(AccountDTO from, AccountDTO to, String amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public AccountDTO getFrom() {
        return from;
    }

    public void setFrom(AccountDTO from) {
        this.from = from;
    }

    public AccountDTO getTo() {
        return to;
    }

    public void setTo(AccountDTO to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransferDTO)) return false;
        TransferDTO that = (TransferDTO) o;
        return Objects.equals(getFrom(), that.getFrom()) &&
                Objects.equals(getTo(), that.getTo()) &&
                Objects.equals(getAmount(), that.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getAmount());
    }
}
