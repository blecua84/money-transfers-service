package com.blecua84.moneytransfers.persistence.daos.impl;

import com.blecua84.moneytransfers.persistence.DataManager;
import com.blecua84.moneytransfers.persistence.daos.AccountDAO;
import com.blecua84.moneytransfers.persistence.daos.CommonDAO;
import com.blecua84.moneytransfers.persistence.daos.TransfersDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.models.Transfer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class DefaultTransfersDAO extends CommonDAO implements TransfersDAO {

    private static DefaultTransfersDAO instance;
    Function<Transfer, Boolean> isTransferNull = transfer -> !ofNullable(transfer).isPresent();
    private DataManager dataManager;
    private AccountDAO accountDAO;
    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    private DefaultTransfersDAO() {
        this.lock = new ReentrantReadWriteLock();
        this.writeLock = this.lock.writeLock();
        this.readLock = this.lock.readLock();
    }

    public static DefaultTransfersDAO getInstance() {
        if (!ofNullable(instance).isPresent()) {
            instance = new DefaultTransfersDAO();
        }
        return instance;
    }

    @Override
    public void saveTransfers(Transfer transfer) throws DataManagerException {
        log.info("Init saveTransfers");
        log.debug("Input Transfer: " + transfer);

        try {
            log.debug("Locking thread...");
            writeLock.lock();

            if (isTransferNull.apply(transfer)) {
                throw new DataManagerException(INPUT_TRANSFER_IS_NULL);
            }

            Transaction transaction = null;

            try (Session session = instance.getDataManager().getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                accountDAO.updateAccount(session, transfer.getFrom());
                accountDAO.updateAccount(session, transfer.getTo());
                session.save(transfer);

                transaction.commit();
            } catch (HibernateException e) {
                rollbackAndThrowException(transaction, e, TRANSFER_CANNOT_BE_SAVED);
            }
        } finally {
            log.debug("Releasing thread...");
            writeLock.unlock();
            log.debug("Thread released!");
        }

        log.debug("Transfer " + transfer.toString() + " has been stored successfully.");
        log.info("End saveTransfers");
    }

    @Override
    public List<Transfer> getTransfers() throws DataManagerException {
        log.info("Init getTransfers");

        List<Transfer> resultList;
        try {
            log.debug("Locking thread...");
            readLock.lock();

            resultList = execNonTransactionalConcreteOperation(
                    instance.getDataManager(),
                    TRANSFER_CANNOT_BE_FETCHED);
        } finally {
            log.debug("Releasing thread...");
            readLock.unlock();
            log.debug("Thread released!");
        }

        log.info("End saveTransfers");
        return resultList;
    }

    @Override
    protected <T> T execConcreteFunction(Session session, String... params) {
        return (T) session.createQuery(GET_ALL_TRANSFERS_QUERY).list();
    }
}
