package com.blecua84.moneytransfers.persistence.daos;

import com.blecua84.moneytransfers.persistence.DataManager;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.BiConsumer;

@Slf4j
public abstract class CommonDAO {

    protected CommonDAO() {
    }

    protected void rollbackAndThrowException(Transaction transaction, HibernateException exception, String message)
            throws DataManagerException {
        log.error(message, exception);

        if (transaction != null) {
            transaction.rollback();
        }

        throw new DataManagerException(message, exception);
    }

    protected <T> void execTransactionalOperation(DataManager instance, String errorMessage, BiConsumer predicate, T data)
            throws DataManagerException {
        log.info("Init execTransactionalOperation");
        Transaction transaction = null;
        try (Session session = instance.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            predicate.accept(session, data);

            transaction.commit();
        } catch (HibernateException e) {
            rollbackAndThrowException(transaction, e, errorMessage);
        }
        log.info("End execTransactionalOperation");
    }

    protected abstract <T> T execConcreteFunction(Session session, String... params) throws DataManagerException;

    protected <T> T execNonTransactionalOperation(DataManager instance, String errorMessage, String... params)
            throws DataManagerException {
        log.info("Init execNonTransactionalOperation");

        T result;
        try (Session session = instance.getSessionFactory().openSession()) {
            result = execConcreteFunction(session, params);
        } catch (HibernateException e) {
            log.error(errorMessage, e);
            throw new DataManagerException(errorMessage, e);
        }

        log.info("End execNonTransactionalOperation");
        return result;
    }
}
