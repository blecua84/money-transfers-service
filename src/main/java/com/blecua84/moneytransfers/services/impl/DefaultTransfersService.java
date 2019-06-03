package com.blecua84.moneytransfers.services.impl;


import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Transfer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultTransfersService implements TransfersService {

    @Override
    public void create(Transfer transfer) throws TransfersException {
        log.info("Here");
    }
}
