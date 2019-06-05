package com.blecua84.moneytransfers.services.impl;


import com.blecua84.moneytransfers.persistence.daos.TransfersDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Transfer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class DefaultTransfersService implements TransfersService {

    private static DefaultTransfersService instance;
    private TransfersDAO transfersDAO;

    private DefaultTransfersService() {
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
        log.debug("Input Transfer to create: " + transfer.toString());

        try {
            transfersDAO.saveTransfers(transfer);
        } catch (DataManagerException e) {
            e.printStackTrace();
        }

        log.info("End create");
    }

    @Override
    public List<Transfer> getTransfers() throws TransfersException {
        log.info("Init getTransfers");
        List<Transfer> transfers = null;
        try {
            transfers = transfersDAO.getTransfers();
        } catch (DataManagerException e) {
            e.printStackTrace();
        }
        log.info("End getTransfers");
        return transfers;
    }
}
