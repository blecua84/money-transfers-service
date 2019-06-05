package com.blecua84.moneytransfers.services;

import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Transfer;

import java.util.List;

public interface TransfersService {

    String ACCOUNT_WITH_NO_ENOUGH_FUNDS = "Account has not enough funds to make the transfer.";
    String TRANSFER_CANNOT_BE_NULL = "Transfer cannot be null.";
    String ACCOUNTS_CANNOT_BE_NULL = "Accounts cannot be null.";
    String ACCOUNTS_ARE_THE_SAME = "Account From and Account To are the same.";

    void create(Transfer transfer) throws TransfersException;

    List<Transfer> getTransfers() throws TransfersException;
}
