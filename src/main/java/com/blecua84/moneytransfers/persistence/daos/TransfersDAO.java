package com.blecua84.moneytransfers.persistence.daos;

import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.models.Transfer;

import java.util.List;

public interface TransfersDAO {

    String GET_ALL_TRANSFERS_QUERY = "from Transfer";
    String TRANSFER_CANNOT_BE_SAVED = "There was an error trying to save the transfer";
    String INPUT_TRANSFER_IS_NULL = "Input Transfer is null or empty";
    String TRANSFER_CANNOT_BE_FETCHED = "There was an error trying to get the transfers list";

    void saveTransfers(Transfer transfer) throws DataManagerException;

    List<Transfer> getTransfers() throws DataManagerException;
}
