package com.blecua84.moneytransfers.persistence.daos;

import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.models.Account;
import org.hibernate.Session;

public interface AccountDAO {

    String GET_ACCOUNT_BY_SORT_CODE_AND_NUMBER_QUERY = "from Account where sortCode=:SC and accountNumber=:AN";
    String ACCOUNT_NULL_MESSAGE = "Input account is null or empty";
    String ACCOUNT_CANNOT_BE_UPDATED = "Input account cannot be updated";
    String ACCOUNT_CANNOT_BE_SAVED = "There was an error trying to save the account";
    String ACCOUNT_CANNOT_BE_FETCHED = "There was an error trying to get the account";
    String ACCOUNT_DOES_NOT_EXIST = "Account does not exist.";

    void saveAccount(Account account) throws DataManagerException;

    Account getAccountBySortCodeAndNumber(String sortCode, String accountNumber) throws DataManagerException;

    void updateAccount(Session session, Account account) throws DataManagerException;
}
