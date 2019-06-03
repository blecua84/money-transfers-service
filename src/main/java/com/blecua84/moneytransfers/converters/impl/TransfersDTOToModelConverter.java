package com.blecua84.moneytransfers.converters.impl;

import com.blecua84.moneytransfers.converters.Converter;
import com.blecua84.moneytransfers.converters.exceptions.ConverterException;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.models.Transfer;

import static java.util.Optional.ofNullable;

public class TransfersDTOToModelConverter implements Converter<TransferDTO, Transfer> {

    private static final String AMOUNT_CONVERSION_ERROR_MESSAGE =
            "There was an error trying to get the amount numeric number.";
    private static TransfersDTOToModelConverter instance;

    private AccountDTOToModelConverter accountDTOToModelConverter;

    public static TransfersDTOToModelConverter getInstance() {
        if (instance == null) {
            instance = new TransfersDTOToModelConverter();
        }

        return instance;
    }

    public void setAccountDTOToModelConverter(AccountDTOToModelConverter accountDTOToModelConverter) {
        this.accountDTOToModelConverter = accountDTOToModelConverter;
    }

    @Override
    public Transfer convert(TransferDTO transferDTO) throws ConverterException {
        Transfer result = null;
        try {
            if (ofNullable(transferDTO).isPresent()) {
                result = new Transfer(
                        this.accountDTOToModelConverter.convert(transferDTO.getFrom()),
                        this.accountDTOToModelConverter.convert(transferDTO.getTo()),
                        Float.parseFloat(transferDTO.getAmount()));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new ConverterException(AMOUNT_CONVERSION_ERROR_MESSAGE);
        }

        return result;
    }
}
