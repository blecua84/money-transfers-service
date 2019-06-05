package com.blecua84.moneytransfers.persistence.daos.impl;

import com.blecua84.moneytransfers.persistence.DataManager;
import com.blecua84.moneytransfers.persistence.daos.AccountDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.models.Transfer;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static com.blecua84.moneytransfers.persistence.daos.TransfersDAO.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultTransfersDAOTest {

    private DefaultTransfersDAO instance;
    private AccountDAO accountDAO;
    private DataManager dataManager;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
    private Query query;

    @BeforeEach
    public void setUp() {
        this.instance = DefaultTransfersDAO.getInstance();
        this.accountDAO = mock(AccountDAO.class);
        this.dataManager = mock(DataManager.class);
        this.sessionFactory = mock(SessionFactory.class);
        this.session = mock(Session.class);
        this.transaction = mock(Transaction.class);
        this.query = mock(Query.class);

        this.instance.setDataManager(dataManager);
        this.instance.setAccountDAO(accountDAO);
    }

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(DefaultTransfersDAO.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        DefaultTransfersDAO firstInstance = DefaultTransfersDAO.getInstance();
        DefaultTransfersDAO secondInstance = DefaultTransfersDAO.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void saveTransfers_whenItReceivesAValidTransfer_shouldBeStoredInTheDataBase() throws DataManagerException {
        doNothing().when(this.accountDAO).saveAccount(any());
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        this.instance.saveTransfers(mock(Transfer.class));

        verify(accountDAO, times(2)).saveAccount(any());
        verify(dataManager, times(1)).getSessionFactory();
        verify(sessionFactory).openSession();
        verify(session).beginTransaction();
        verify(transaction).commit();
    }

    @Test
    void saveTransfers_whenItReceivesANullTransferObject_shouldThrowAnException() {
        try {
            this.instance.saveTransfers(null);
        } catch (DataManagerException e) {
            assertEquals("Input Transfer is null or empty", e.getMessage());
        }
    }

    @Test
    void saveTransfers_whenItReceivesAValidTransferButThereCannotOpenTheSession_shouldThrowAnException() {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        doThrow(mock(HibernateException.class)).when(sessionFactory).openSession();

        try {
            this.instance.saveTransfers(mock(Transfer.class));
            fail();
        } catch (DataManagerException e) {
            assertEquals(TRANSFER_CANNOT_BE_SAVED, e.getMessage());
        }
    }

    @Test
    void saveTransfers_whenItReceivesAValidTransferButThereIsAnErrorWhenTriesToSave_shouldExecuteRollbackAndThrowAnException()
            throws DataManagerException {
        doNothing().when(this.accountDAO).saveAccount(any());
        doThrow(mock(HibernateException.class)).when(session).save(any());
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        try {
            this.instance.saveTransfers(mock(Transfer.class));
            fail();
        } catch (DataManagerException e) {
            verify(transaction).rollback();
            assertEquals(TRANSFER_CANNOT_BE_SAVED, e.getMessage());
        }
    }

    @Test
    void getTransfers_whenItIsInvoked_shouldReturnAllTransfersStored() throws DataManagerException {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(eq(GET_ALL_TRANSFERS_QUERY))).thenReturn(query);
        when(query.list()).thenReturn(Collections.EMPTY_LIST);

        List<Transfer> result = this.instance.getTransfers();

        assertEquals(result, Collections.EMPTY_LIST);
        verify(sessionFactory).openSession();
        verify(session).createQuery(eq(GET_ALL_TRANSFERS_QUERY));
        verify(query).list();
    }

    @Test
    void getTransfers_whenItIsInvokedAndTheSessionIsNotOpened_shouldThrowAnException() {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        doThrow(mock(HibernateException.class)).when(sessionFactory).openSession();

        try {
            this.instance.getTransfers();
            fail();
        } catch (DataManagerException e) {
            assertEquals(TRANSFER_CANNOT_BE_FETCHED, e.getMessage());
        }
    }
}
