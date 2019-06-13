package com.blecua84.moneytransfers.persistence.daos.impl;

import com.blecua84.moneytransfers.persistence.DataManager;
import com.blecua84.moneytransfers.persistence.daos.AccountDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.models.Account;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultAccountDAOTest {

    private DefaultAccountDAO instance;
    private DataManager dataManager;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
    private Query query;

    @BeforeEach
    void setUp() {
        this.instance = DefaultAccountDAO.getInstance();
        this.dataManager = mock(DataManager.class);
        this.sessionFactory = mock(SessionFactory.class);
        this.session = mock(Session.class);
        this.transaction = mock(Transaction.class);
        this.query = mock(Query.class);

        this.instance.setDataManager(dataManager);
    }

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(DefaultAccountDAO.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        DefaultAccountDAO firstInstance = DefaultAccountDAO.getInstance();
        DefaultAccountDAO secondInstance = DefaultAccountDAO.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void saveAccount_whenInputAccountIsNull_shouldThrownDataManagerException() {
        try {
            instance.saveAccount(null);
            fail();
        } catch (DataManagerException e) {
            assertEquals(instance.ACCOUNT_NULL_MESSAGE, e.getMessage());
        }
    }

    @Test
    void saveAccount_whenInputAccountIsValidButThereIsNoDataManager_shouldThrownDataManagerException() {
        instance.setDataManager(null);

        try {
            instance.saveAccount(new Account("010101", "12345678", new BigDecimal(100)));
            fail();
        } catch (DataManagerException e) {
            assertEquals(AccountDAO.ACCOUNT_CANNOT_BE_SAVED, e.getMessage());
        }
    }

    @Test
    void saveAccount_whenInputAccountIsValidButThereIsNoSessionFactory_shouldThrownDataManagerException() {
        when(dataManager.getSessionFactory()).thenReturn(null);

        try {
            instance.saveAccount(new Account("010101", "12345678", new BigDecimal(100)));
            fail();
        } catch (DataManagerException e) {
            assertEquals(AccountDAO.ACCOUNT_CANNOT_BE_SAVED, e.getMessage());
        }
    }

    @Test
    void saveAccount_whenInputAccountIsValidButThereIsAndErrorOpeningTheSession_shouldThrownDataManagerException() {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenThrow(mock(HibernateException.class));

        try {
            instance.saveAccount(new Account("010101", "12345678", new BigDecimal(100)));
            fail();
        } catch (DataManagerException e) {
            assertEquals(AccountDAO.ACCOUNT_CANNOT_BE_SAVED, e.getMessage());
        }
    }

    @Test
    void saveAccount_whenInputAccountIsValidButThereIsAnError_shouldDoRollback() {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(mock(HibernateException.class)).when(session).save(any());

        try {
            instance.saveAccount(new Account("010101", "12345678", new BigDecimal(100)));
            fail();
        } catch (DataManagerException e) {
            assertEquals(AccountDAO.ACCOUNT_CANNOT_BE_SAVED, e.getMessage());
            verify(instance.getDataManager(), times(2)).getSessionFactory();
            verify(dataManager, times(2)).getSessionFactory();
            verify(sessionFactory).openSession();
            verify(session).beginTransaction();
            verify(transaction).rollback();
        }
    }

    @Test
    void saveAccount_whenInputAccountIsValid_shouldSaveData() {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        try {
            instance.saveAccount(new Account("010101", "12345678", new BigDecimal(100)));

            verify(instance.getDataManager(), times(2)).getSessionFactory();
            verify(dataManager, times(2)).getSessionFactory();
            verify(sessionFactory).openSession();
            verify(session).beginTransaction();
            verify(transaction).commit();
        } catch (DataManagerException e) {
            fail();
        }
    }

    @Test
    void getAccountBySortCodeAndNumber_whenSortCodeAndAccountNumberExists_shouldReturnAValidAccount()
            throws DataManagerException {

        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        Object mockResult = mock(Account.class);
        when(query.getSingleResult()).thenReturn(mockResult);

        Account result = instance.getAccountBySortCodeAndNumber("010203", "123445678");

        assertNotNull(result);
        assertEquals(mockResult, result);
    }

    @Test
    void getAccountBySortCodeAndNumber_whenSortCodeAndAccountNumberDoesNotExist_shouldThrownADataManagerException() {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        try {
            instance.getAccountBySortCodeAndNumber("010203", "123445678");
            fail();
        } catch (DataManagerException e) {
            assertEquals(AccountDAO.ACCOUNT_DOES_NOT_EXIST, e.getMessage());
        }
    }

    @Test
    void getAccountBySortCodeAndNumber_ifThereIsAnErrorOpeningSession_shouldThrownADataManagerException() {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenThrow(mock(HibernateException.class));

        try {
            instance.getAccountBySortCodeAndNumber("010203", "123445678");
            fail();
        } catch (DataManagerException e) {
            assertEquals(AccountDAO.ACCOUNT_CANNOT_BE_FETCHED, e.getMessage());
        }
    }

    @Test
    void updateAccount_whenTheInputIsNull_shouldThrownADataManagerException() {
        try {
            instance.updateAccount(session, null);
        } catch (DataManagerException e) {
            assertEquals(instance.ACCOUNT_NULL_MESSAGE, e.getMessage());
        }
    }

    @Test
    void updateAccount_whenTheAccountIsValid_shouldUpdateTheAccount() throws DataManagerException {
        when(dataManager.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        Object mockResult = mock(Account.class);
        when(query.getSingleResult()).thenReturn(mockResult);
        when(session.beginTransaction()).thenReturn(transaction);

        instance.updateAccount(session, new Account(1, "010101", "12345678", new BigDecimal(100)));

        verify(session).update(any());
    }

    @Test
    void updateAccount_whenTheAccountIsValidButThereIsAnErrorInTheUpdateOperation_shouldThrownADataManagerException() {
        when(session.createQuery(anyString())).thenReturn(query);
        Object mockResult = mock(Account.class);
        when(query.getSingleResult()).thenReturn(mockResult);

        doThrow(HibernateException.class).when(session).update(any());

        try {
            instance.updateAccount(session, new Account(1, "010101", "12345678", new BigDecimal(100)));
        } catch (DataManagerException e) {
            verify(session).update(any());
            assertEquals(instance.ACCOUNT_CANNOT_BE_UPDATED, e.getMessage());
        }
    }
}
