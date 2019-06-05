package com.blecua84.moneytransfers.services.impl;

import com.blecua84.moneytransfers.persistence.daos.AccountDAO;
import com.blecua84.moneytransfers.persistence.daos.TransfersDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Account;
import com.blecua84.moneytransfers.services.models.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static com.blecua84.moneytransfers.services.TransfersService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultTransfersServiceTest {

    private TransfersDAO transfersDAO;
    private AccountDAO accountDAO;
    private DefaultTransfersService transfersService;
    private Transfer transferToDo;

    @BeforeEach
    void setUp() {
        this.transfersDAO = mock(TransfersDAO.class);
        this.accountDAO = mock(AccountDAO.class);

        this.transfersService = DefaultTransfersService.getInstance();
        this.transfersService.setTransfersDAO(this.transfersDAO);
        this.transfersService.setAccountDAO(this.accountDAO);

        transferToDo = new Transfer(new Account("123456", "12345678", 5000),
                new Account("123256", "12340008", 3500.10F), 100);
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
    void createTransfer_whenInputIsNull_shouldThrowAnException() {
        try {
            this.transfersService.create(null);
            fail();
        } catch (TransfersException e) {
            assertEquals(TRANSFER_CANNOT_BE_NULL, e.getMessage());
        }
    }

    @Test
    void createTransfer_whenBothAccountsAreNull_shouldThrowAnException() {
        transferToDo.setFrom(null);
        transferToDo.setTo(null);

        try {
            this.transfersService.create(transferToDo);
            fail();
        } catch (TransfersException e) {
            assertEquals(ACCOUNTS_CANNOT_BE_NULL, e.getMessage());
        }
    }

    @Test
    void createTransfer_whenInputIsCorrectButAccountFromIsNotInTheSystem_shouldThrowAnException()
            throws DataManagerException {
        doThrow(new DataManagerException("Account not found."))
                .when(this.accountDAO).getAccountBySortCodeAndNumber(any(), any());

        try {
            this.transfersService.create(transferToDo);
            fail();
        } catch (TransfersException e) {
            assertEquals("Account not found.", e.getMessage());
        }
    }

    @Test
    void createTransfer_whenInputIsCorrectButAccountToIsNotInTheSystem_shouldThrowAnException()
            throws DataManagerException {
        when(this.accountDAO.getAccountBySortCodeAndNumber(any(), any()))
                .thenReturn(mock(Account.class))
                .thenThrow(new DataManagerException("Account not found."));

        try {
            this.transfersService.create(transferToDo);
            fail();
        } catch (TransfersException e) {
            assertEquals("Account not found.", e.getMessage());
            verify(this.accountDAO, times(2)).getAccountBySortCodeAndNumber(any(), any());
        }
    }

    @Test
    void createTransfer_whenAccountFromIsTheSameThanAccountToo_shouldThrowAnException() {
        Transfer transferWithSameAccounts = new Transfer(
                new Account("123456", "12345678", 120),
                new Account("123456", "12345678", 120),
                100);

        try {
            this.transfersService.create(transferWithSameAccounts);
            fail();
        } catch (TransfersException e) {
            assertEquals(ACCOUNTS_ARE_THE_SAME, e.getMessage());
        }
    }

    @Test
    void createTransfer_whenAccountFromHasNotEnoughFundsToDoTheTransfer_shouldThrowAnException()
            throws DataManagerException {
        when(this.accountDAO.getAccountBySortCodeAndNumber(any(), any()))
                .thenReturn(new Account(1, "123456", "12345678", 50))
                .thenReturn(new Account(2, "123456", "12345678", 120));

        try {
            this.transfersService.create(transferToDo);
            fail();
        } catch (TransfersException e) {
            assertEquals(ACCOUNT_WITH_NO_ENOUGH_FUNDS, e.getMessage());
            verify(this.accountDAO, times(2)).getAccountBySortCodeAndNumber(any(), any());
        }
    }

    @Test
    void createTransfer_whenAccountsAreCorrect_shouldUpdateTheirNewAmountAvailable()
            throws DataManagerException, TransfersException {
        when(this.accountDAO.getAccountBySortCodeAndNumber(any(), any()))
                .thenReturn(new Account(1, "123456", "12345678", 5000.25F))
                .thenReturn(new Account(2, "123456", "12345678", 1200.00F));

        this.transfersService.create(transferToDo);

        verify(this.transfersDAO).saveTransfers(
                eq(new Transfer(new Account(1, "123456", "12345678", 4900.25F),
                        new Account(2, "123456", "12345678", 1300.00F), 100)));
    }

    @Test
    void getTransfers_whenItIsInvoked_shouldReturnAllTheCreatedTransfers() throws TransfersException,
            DataManagerException {
        List<Transfer> mockList = Collections.singletonList(mock(Transfer.class));
        when(this.transfersDAO.getTransfers()).thenReturn(mockList);

        List<Transfer> resultList = this.transfersService.getTransfers();

        assertEquals(mockList, resultList);
        verify(this.transfersDAO).getTransfers();
    }
}
