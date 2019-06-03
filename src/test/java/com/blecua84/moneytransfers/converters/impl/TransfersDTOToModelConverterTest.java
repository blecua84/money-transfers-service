package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.converters.exceptions.ConverterException;
import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.models.Account;
import com.blecua84.moneytransfers.services.models.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransfersDTOToModelConverterTest {

    private TransfersDTOToModelConverter converter;
    private AccountDTOToModelConverter accountDTOConverter;

    @BeforeEach
    void setUp() {
        this.converter = TransfersDTOToModelConverter.getInstance();
        this.accountDTOConverter = mock(AccountDTOToModelConverter.class);

        this.converter.setAccountDTOToModelConverter(this.accountDTOConverter);
    }

    @Test
    void getInstance_shouldCreateANewObjectWithAccountConverterAsAParameter() {
        assertNotNull(TransfersDTOToModelConverter.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        TransfersDTOToModelConverter firstInstance = TransfersDTOToModelConverter.getInstance();
        TransfersDTOToModelConverter secondInstance = TransfersDTOToModelConverter.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void convert_whenInputParameterContainsValidToAndFromObjects_shouldCallToTransferConverterTwice() throws ConverterException {
        TransferDTO transferDTO = new TransferDTO(new AccountDTO("020202", "13121415"),
                new AccountDTO("020202", "13121415"),
                "10.5");
        when(this.accountDTOConverter.convert(any(AccountDTO.class))).thenReturn(
                new Account("01", "02", 0F));

        this.converter.convert(transferDTO);

        verify(this.accountDTOConverter, times(2)).convert(any(AccountDTO.class));
    }

    @Test
    void convert_whenInputParameterContainsAValidAmount_shouldConvertToAValidFloatFromString() throws ConverterException {
        TransferDTO transferDTO = new TransferDTO(null, null, "10.5");

        Transfer result = converter.convert(transferDTO);

        assertEquals(10.5F, result.getAmount());
    }

    @Test
    void convert_whenAmountIsNotValidAsAFloat_shouldThrowAConverterException() {
        TransferDTO transferDTO = new TransferDTO(null, null, "NOT VALID FLOAT");

        try {
            converter.convert(transferDTO);
            fail();
        } catch (ConverterException ex) {
            assertEquals("There was an error trying to get the amount numeric number.", ex.getMessage());
        }
    }

    @Test
    void convert_whenInputObjectIsNull_shouldReturnNull() throws ConverterException {
        assertNull(converter.convert(null));
    }
}
