package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.converters.Converter;
import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.services.models.Account;

import static java.util.Optional.ofNullable;

public class AccountToDTOModelConverter implements Converter<Account, AccountDTO> {

    private static AccountToDTOModelConverter instance;

    private AccountToDTOModelConverter() {
    }

    public static AccountToDTOModelConverter getInstance() {
        if (ofNullable(instance).isEmpty()) {
            instance = new AccountToDTOModelConverter();
        }
        return instance;
    }

    @Override
    public AccountDTO convert(Account account) {
        AccountDTO targetAccount = null;

        if (ofNullable(account).isPresent()) {
            targetAccount = new AccountDTO(
                    account.getSortCode(),
                    account.getAccountNumber(),
                    account.getAvailable().toPlainString());
        }

        return targetAccount;
    }
}
