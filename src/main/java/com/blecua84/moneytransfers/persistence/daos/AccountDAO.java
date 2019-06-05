package com.blecua84.moneytransfers.persistence.daos;

import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.models.Account;

public interface AccountDAO {

    public String GET_ACCOUNT_BY_SORT_CODE_AND_NUMBER_QUERY = "from Account where sortCode=:SC and accountNumber=:AN";
    public String ACCOUNT_NULL_MESSAGE = "Input account is null or empty";
    public String ACCOUNT_CANNOT_BE_UPDATED = "Input account cannot be updated";
    public String ACCOUNT_CANNOT_BE_SAVED = "There was an error trying to save the account";
    public String ACCOUNT_CANNOT_BE_FETCHED = "There was an error trying to get the account";
    public String ACCOUNT_DOES_NOT_EXIST = "Account does not exist.";

    public void saveAccount(Account account) throws DataManagerException;

    public Account getAccountBySortCodeAndNumber(String sortCode, String accountNumber) throws DataManagerException;

    public void updateAccount(Account account) throws DataManagerException;
}
