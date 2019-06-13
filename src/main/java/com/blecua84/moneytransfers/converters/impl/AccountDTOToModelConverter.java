package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.converters.Converter;
import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.services.models.Account;

import java.math.BigDecimal;

public class AccountDTOToModelConverter implements Converter<AccountDTO, Account> {

    private static AccountDTOToModelConverter instance;

    private AccountDTOToModelConverter() {
    }

    public static AccountDTOToModelConverter getInstance() {
        if (instance == null) {
            instance = new AccountDTOToModelConverter();
        }

        return instance;
    }

    @Override
    public Account convert(AccountDTO accountDTO) {
        Account account = null;

        if (accountDTO != null) {
            account = new Account(accountDTO.getSortCode(), accountDTO.getAccountNumber(), new BigDecimal(0));
        }

        return account;
    }
}
