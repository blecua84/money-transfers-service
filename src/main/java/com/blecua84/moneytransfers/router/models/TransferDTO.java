package com.blecua84.moneytransfers.router.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferDTO {

    public static final String FROM_ERROR_MSG = "Parameter 'from' cannot be null.";
    public static final String TO_ERROR_MSG = "Parameter 'to' cannot be null.";
    public static final String AMOUNT_ERROR_MSG =
            "Parameter 'amount' of the transaction is not valid (8 digits and 2 decimals).";
    public static final String AMOUNT_REGEX = "^(\\d{1,8})((.)(\\d{1,2}){0,1})$";

    @NotNull(message = FROM_ERROR_MSG)
    @Valid
    private AccountDTO from;

    @NotNull(message = TO_ERROR_MSG)
    @Valid
    private AccountDTO to;

    @NotBlank(message = AMOUNT_ERROR_MSG)
    @Pattern(regexp = AMOUNT_REGEX, message = AMOUNT_ERROR_MSG)
    private String amount;
}
