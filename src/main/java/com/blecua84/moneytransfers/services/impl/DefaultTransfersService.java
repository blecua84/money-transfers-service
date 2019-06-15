package com.blecua84.moneytransfers.services.impl;


import com.blecua84.moneytransfers.persistence.daos.AccountDAO;
import com.blecua84.moneytransfers.persistence.daos.TransfersDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Account;
import com.blecua84.moneytransfers.services.models.Transfer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Optional.ofNullable;

@Slf4j
@Data
public class DefaultTransfersService implements TransfersService {

    private static DefaultTransfersService instance;
    private TransfersDAO transfersDAO;
    private AccountDAO accountDAO;
    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    private DefaultTransfersService() {
        this.lock = new ReentrantReadWriteLock();
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();
    }

    public static DefaultTransfersService getInstance() {
        if (instance == null) {
            instance = new DefaultTransfersService();
        }
        return instance;
    }

    @Override
    public void create(Transfer transfer) throws TransfersException {
        log.info("Init create");
        try {
            log.debug("Locking thread...");
            writeLock.lock();

            checkInputParameter(transfer);

            log.debug("Input Transfer to create: " + transfer.toString());

            try {
                fetchAccountFromDB(transfer);

                if (!hasAccountFromEnoughMoney(transfer)) {
                    log.error(ACCOUNT_WITH_NO_ENOUGH_FUNDS);
                    throw new TransfersException(ACCOUNT_WITH_NO_ENOUGH_FUNDS);
                }

                updateAccountsWithTheAmountOfTransfer(transfer);
                log.debug("Transfer to store: " + transfer.toString());
                transfersDAO.saveTransfers(transfer);
            } catch (DataManagerException e) {
                log.error(e.getMessage(), e);
                throw new TransfersException(e.getMessage(), e);
            }
        } finally {
            log.debug("Releasing thread...");
            writeLock.unlock();
            log.debug("Thread released!");
        }

        log.info("End create");
    }

    @Override
    public List<Transfer> getTransfers() throws TransfersException {
        log.info("Init getTransfers");

        List<Transfer> transfers;
        try {
            log.debug("Locking thread...");
            readLock.lock();

            try {
                transfers = transfersDAO.getTransfers();
            } catch (DataManagerException e) {
                log.error(e.getMessage(), e);
                throw new TransfersException(e.getMessage(), e);
            }
        } finally {
            log.debug("Releasing thread...");
            readLock.unlock();
            log.debug("Thread released!");
        }
        log.info("End getTransfers");
        return transfers;
    }

    private void checkInputParameter(Transfer transfer) throws TransfersException {
        if (ofNullable(transfer).isEmpty()) {
            log.error(TRANSFER_CANNOT_BE_NULL);
            throw new TransfersException(TRANSFER_CANNOT_BE_NULL);
        }

        if (ofNullable(transfer.getFrom()).isEmpty() || ofNullable(transfer.getTo()).isEmpty()) {
            log.error(ACCOUNTS_CANNOT_BE_NULL);
            throw new TransfersException(ACCOUNTS_CANNOT_BE_NULL);
        }

        if (isBothAccountsEquals(transfer.getFrom(), transfer.getTo())) {
            log.error(ACCOUNTS_ARE_THE_SAME);
            throw new TransfersException(ACCOUNTS_ARE_THE_SAME);
        }
    }

    private boolean hasAccountFromEnoughMoney(Transfer transfer) {
        return transfer.getFrom().getAvailable().compareTo(transfer.getAmount()) > 0;
    }

    private void fetchAccountFromDB(Transfer transfer) throws DataManagerException {
        transfer.setFrom(fetchAccountFromDB(transfer.getFrom()));
        transfer.setTo(fetchAccountFromDB(transfer.getTo()));

    }

    private void updateAccountsWithTheAmountOfTransfer(Transfer transfer) {
        transfer.setFrom(takeOutMoneyFromAccount(transfer.getFrom(), transfer.getAmount()));
        transfer.setTo(takeInMoneyToAccount(transfer.getTo(), transfer.getAmount()));

    }

    private Account takeOutMoneyFromAccount(Account account, BigDecimal amount) {
        account.setAvailable(account.getAvailable().subtract(amount));

        return account;
    }

    private Account takeInMoneyToAccount(Account account, BigDecimal amount) {
        account.setAvailable(account.getAvailable().add(amount));

        return account;
    }

    private boolean isBothAccountsEquals(Account accountFrom, Account accountTo) {
        return accountFrom.getSortCode().equals(accountTo.getSortCode()) &&
                accountFrom.getAccountNumber().equals(accountTo.getAccountNumber());
    }

    private Account fetchAccountFromDB(Account input) throws DataManagerException {
        return this.accountDAO.getAccountBySortCodeAndNumber(input.getSortCode(), input.getAccountNumber());
    }
}
