package com.blecua84.moneytransfers.router.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO<T> {
    private int status;
    private String message;
    private T data;

    public ResultDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
