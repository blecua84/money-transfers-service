package com.blecua84.moneytransfers.services.impl;

import com.blecua84.moneytransfers.persistence.daos.TransfersDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultTransfersServiceTest {

    private TransfersDAO transfersDAO;
    private DefaultTransfersService transfersService;

    @BeforeEach
    void setUp() {
        this.transfersDAO = mock(TransfersDAO.class);

        this.transfersService = DefaultTransfersService.getInstance();
        this.transfersService.setTransfersDAO(this.transfersDAO);
    }

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(DefaultTransfersService.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        DefaultTransfersService firstInstance = DefaultTransfersService.getInstance();
        DefaultTransfersService secondInstance = DefaultTransfersService.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void createTransfer_whenInputIsCorrect_shouldCreateANewTransfer() throws DataManagerException, TransfersException {
        doNothing().when(this.transfersDAO).saveTransfers(any());
        Transfer transferToDo = mock(Transfer.class);

        this.transfersService.create(transferToDo);

        verify(this.transfersDAO).saveTransfers(eq(transferToDo));
    }

    @Test
    void getTransfers_whenItIsInvoked_shouldReturnAllTheCreatedTransfers() throws TransfersException, DataManagerException {
        List<Transfer> mockList = Collections.singletonList(mock(Transfer.class));
        when(this.transfersDAO.getTransfers()).thenReturn(mockList);

        List<Transfer> resultList = this.transfersService.getTransfers();

        assertEquals(mockList, resultList);
        verify(this.transfersDAO).getTransfers();
    }
}
