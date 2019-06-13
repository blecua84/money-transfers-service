package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.converters.Converter;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.models.Transfer;

import java.util.LinkedList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class TransferListToTransferDTOConverter implements Converter<List<Transfer>, List<TransferDTO>> {

    private static TransferListToTransferDTOConverter instance;
    private AccountToDTOModelConverter accountToDTOModelConverter;

    private TransferListToTransferDTOConverter() {
    }

    public static TransferListToTransferDTOConverter getInstance() {
        if (ofNullable(instance).isEmpty()) {
            instance = new TransferListToTransferDTOConverter();
        }
        return instance;
    }

    public void setAccountToDTOModelConverter(AccountToDTOModelConverter accountToDTOModelConverter) {
        this.accountToDTOModelConverter = accountToDTOModelConverter;
    }

    @Override
    public List<TransferDTO> convert(List<Transfer> sourceTransfers) {

        List<TransferDTO> targetTransfers = null;

        if (ofNullable(sourceTransfers).isPresent()) {

            targetTransfers = new LinkedList<>();

            for (Transfer transfer : sourceTransfers) {
                targetTransfers.add(new TransferDTO(
                        accountToDTOModelConverter.convert(transfer.getFrom()),
                        accountToDTOModelConverter.convert(transfer.getTo()),
                        transfer.getAmount().toPlainString()));
            }
        }

        return targetTransfers;
    }
}
