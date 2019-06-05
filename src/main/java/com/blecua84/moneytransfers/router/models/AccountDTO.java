package com.blecua84.moneytransfers.router.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    public static final String SORT_CODE_NOT_VALID_MSG = "Parameter 'sortCode' not valid (6 digits).";
    public static final String ACCOUNT_NUMBER_NOT_VALID_MSG = "Parameter 'accountNumber' not valid (8 digits).";
    public static final String SORT_CODE_REGEX = "^(\\d{6})$";
    public static final String ACCOUNT_NUMBER_REGEX = "^(\\d{8})$";
    @NotNull(message = SORT_CODE_NOT_VALID_MSG)
    @Pattern(regexp = SORT_CODE_REGEX, message = SORT_CODE_NOT_VALID_MSG)
    private String sortCode;
    @NotNull(message = ACCOUNT_NUMBER_NOT_VALID_MSG)
    @Pattern(regexp = ACCOUNT_NUMBER_REGEX, message = ACCOUNT_NUMBER_NOT_VALID_MSG)
    private String accountNumber;
    private String available;

    public AccountDTO(@NotNull(message = SORT_CODE_NOT_VALID_MSG)
                      @Pattern(regexp = SORT_CODE_REGEX, message = SORT_CODE_NOT_VALID_MSG)
                              String sortCode,
                      @NotNull(message = ACCOUNT_NUMBER_NOT_VALID_MSG)
                      @Pattern(regexp = ACCOUNT_NUMBER_REGEX, message = ACCOUNT_NUMBER_NOT_VALID_MSG)
                              String accountNumber) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
    }
}
