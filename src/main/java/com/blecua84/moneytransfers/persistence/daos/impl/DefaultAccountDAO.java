package com.blecua84.moneytransfers.persistence.daos.impl;

import com.blecua84.moneytransfers.persistence.DataManager;
import com.blecua84.moneytransfers.persistence.daos.AccountDAO;
import com.blecua84.moneytransfers.persistence.daos.CommonDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.models.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class DefaultAccountDAO extends CommonDAO implements AccountDAO {

    private static DefaultAccountDAO instance;
    private static Function<DefaultAccountDAO, Boolean> isInstanceNull = instance -> ofNullable(instance).isEmpty();
    private DataManager dataManager;
    private Function<Object, Boolean> isObjectNull = input -> ofNullable(input).isEmpty();
    private Function<Account, Boolean> isAccountNull = account -> isObjectNull.apply(account);
    private Function<DataManager, Boolean> isDataManagerNull = manager -> isObjectNull.apply(manager);
    private Function<SessionFactory, Boolean> isSessionFactoryNull = factory -> isObjectNull.apply(factory);
    private BiConsumer<Session, Account> saveAccount = Session::save;
    private BiConsumer<Session, Account> updateAccount = Session::update;
    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    private DefaultAccountDAO() {
        super();
        this.lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();
    }

    public static DefaultAccountDAO getInstance() {
        if (isInstanceNull.apply(instance)) {
            instance = new DefaultAccountDAO();
        }
        return instance;
    }

    @Override
    public void saveAccount(Account account) throws DataManagerException {
        log.info("Init saveAccount");

        try {
            log.debug("Locking thread...");
            writeLock.lock();

            if (isAccountNull.apply(account)) {
                throw new DataManagerException(ACCOUNT_NULL_MESSAGE);
            }

            if (isInstanceNull.apply(instance) || isDataManagerNull.apply(instance.getDataManager())) {
                throw new DataManagerException(ACCOUNT_CANNOT_BE_SAVED);
            }

            if (isSessionFactoryNull.apply(instance.getDataManager().getSessionFactory())) {
                throw new DataManagerException(ACCOUNT_CANNOT_BE_SAVED);
            }

            log.debug("Account: " + account.toString());
            execTransactionalOperation(instance.getDataManager(), ACCOUNT_CANNOT_BE_SAVED, saveAccount, account);
        } finally {
            log.debug("Releasing thread...");
            writeLock.unlock();
            log.debug("Thread released!");
        }

        log.info("Account successfully saved");
        log.info("End saveAccount");
    }

    @Override
    public Account getAccountBySortCodeAndNumber(String sortCode, String accountNumber) throws DataManagerException {
        log.info("Init getAccountBySortCodeAndNumber");
        log.debug("sortCode: " + sortCode);
        log.debug("accountNumber: " + accountNumber);

        Account result;
        try {
            log.debug("Locking thread...");
            readLock.lock();

            result = execNonTransactionalConcreteOperation(
                    instance.getDataManager(), ACCOUNT_CANNOT_BE_FETCHED, sortCode, accountNumber);
        } finally {
            log.debug("Releasing thread...");
            readLock.unlock();
            log.debug("Thread released!");
        }

        log.info("End getAccountBySortCodeAndNumber");
        return result;
    }

    @Override
    public void updateAccount(Session session, Account account) throws DataManagerException {
        log.info("Init updateAccount");

        try {
            log.debug("Locking thread...");
            writeLock.lock();

            if (isAccountNull.apply(account)) {
                throw new DataManagerException(ACCOUNT_NULL_MESSAGE);
            }

            execNonExplicitTransactionalOperation(session, ACCOUNT_CANNOT_BE_UPDATED, updateAccount, account);
        } finally {
            log.debug("Releasing thread...");
            writeLock.unlock();
            log.debug("Thread released!");
        }

        log.debug("Account: " + account.toString());
        log.info("End updateAccount");
    }

    @Override
    protected <T> T execConcreteFunction(Session session, String[] args)
            throws DataManagerException {
        Account result;
        Query query = session.createQuery(GET_ACCOUNT_BY_SORT_CODE_AND_NUMBER_QUERY);
        query.setParameter("SC", args[0]);
        query.setParameter("AN", args[1]);

        try {
            if (isObjectNull.apply(query.getSingleResult())) {
                log.error(ACCOUNT_DOES_NOT_EXIST);
                throw new DataManagerException(ACCOUNT_DOES_NOT_EXIST);
            }
        } catch (NoResultException e) {
            log.error(ACCOUNT_DOES_NOT_EXIST, e);
            throw new DataManagerException(ACCOUNT_DOES_NOT_EXIST, e);
        }

        result = (Account) query.getSingleResult();
        log.debug("Account retrieved: " + result.toString());
        return (T) result;
    }
}
