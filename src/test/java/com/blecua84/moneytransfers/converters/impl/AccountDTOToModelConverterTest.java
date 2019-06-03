package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.services.models.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountDTOToModelConverterTest {

    private AccountDTOToModelConverter converter;

    @BeforeEach
    void setUp() {
        this.converter = AccountDTOToModelConverter.getInstance();
    }

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(AccountDTOToModelConverter.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        AccountDTOToModelConverter firstInstance = AccountDTOToModelConverter.getInstance();
        AccountDTOToModelConverter secondInstance = AccountDTOToModelConverter.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void convert_whenTheInputIsCorrect_shouldCreateANewObjectWithTheSameInformation() {
        AccountDTO accountDTO = new AccountDTO("010101", "12345678");

        Account result = converter.convert(accountDTO);

        assertEquals(accountDTO.getSortCode(), result.getSortCode());
        assertEquals(accountDTO.getAccountNumber(), result.getAccountNumber());
        assertEquals(0f, result.getAvailable());
    }

    @Test
    void convert_whenTheInputIsNull_shouldReturnNull() {
        assertNull(converter.convert(null));
    }
}
