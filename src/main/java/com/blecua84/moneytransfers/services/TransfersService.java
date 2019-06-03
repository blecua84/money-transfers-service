package com.blecua84.moneytransfers.services;

import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Transfer;

public interface TransfersService {

    void create(Transfer transfer) throws TransfersException;
}
