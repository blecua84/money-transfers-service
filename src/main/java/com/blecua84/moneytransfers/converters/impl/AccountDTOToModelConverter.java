package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.converters.Converter;
import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.services.models.Account;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class AccountDTOToModelConverter implements Converter<AccountDTO, Account> {

    private static final int SCALE_FACTOR = 2;
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
            account = new Account(accountDTO.getSortCode(), accountDTO.getAccountNumber(), createZeroBigDecimal());
        }

        return account;
    }

    private BigDecimal createZeroBigDecimal() {
        BigDecimal amount = new BigDecimal(0, MathContext.UNLIMITED);
        return amount.setScale(SCALE_FACTOR, RoundingMode.HALF_EVEN);
    }
}
