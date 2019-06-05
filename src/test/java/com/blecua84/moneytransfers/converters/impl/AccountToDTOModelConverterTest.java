package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.services.models.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountToDTOModelConverterTest {

    private final AccountToDTOModelConverter converter = AccountToDTOModelConverter.getInstance();

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(AccountToDTOModelConverter.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        AccountToDTOModelConverter firstInstance = AccountToDTOModelConverter.getInstance();
        AccountToDTOModelConverter secondInstance = AccountToDTOModelConverter.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void convert_whenInputIsNull_shouldReturnNull() {
        assertNull(converter.convert(null));
    }

    @Test
    void convert_whenInputIsNotNull_shouldReturnANewObjectWithSameInformation() {
        Account sourceAccount = new Account(1, "010101", "12345678", 1000);

        AccountDTO targetAccount = converter.convert(sourceAccount);

        assertNotNull(targetAccount);
        assertEquals(sourceAccount.getSortCode(), targetAccount.getSortCode());
        assertEquals(sourceAccount.getAccountNumber(), targetAccount.getAccountNumber());
        assertEquals(Float.toString(sourceAccount.getAvailable()), targetAccount.getAvailable());
    }
}
