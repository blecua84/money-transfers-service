package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.models.Account;
import com.blecua84.moneytransfers.services.models.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransferListToTransferDTOConverterTest {

    private TransferListToTransferDTOConverter converter;

    @BeforeEach
    void setUp() {
        this.converter = TransferListToTransferDTOConverter.getInstance();
    }

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(TransferListToTransferDTOConverter.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        TransferListToTransferDTOConverter firstInstance = TransferListToTransferDTOConverter.getInstance();
        TransferListToTransferDTOConverter secondInstance = TransferListToTransferDTOConverter.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void convert_whenInputIsNull_shouldReturnNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    void convert_whenInputIsCorrect_shouldReturnTheSameNumbersOfElementThanTheInput() {
        List<Transfer> sourceList = Collections.singletonList(new Transfer(1,
                mock(Account.class), mock(Account.class), 100));
        AccountToDTOModelConverter mockAccountConverter = mock(AccountToDTOModelConverter.class);
        this.converter.setAccountToDTOModelConverter(mockAccountConverter);

        List<TransferDTO> targetList = this.converter.convert(sourceList);

        assertNotNull(targetList);
        assertEquals(sourceList.size(), targetList.size());
        verify(mockAccountConverter, times(2)).convert(any());
    }
}
