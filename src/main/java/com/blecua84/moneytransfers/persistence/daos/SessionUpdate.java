package com.blecua84.moneytransfers.persistence.daos;

public interface SessionUpdate<T> {

    void exec(T data);
}
