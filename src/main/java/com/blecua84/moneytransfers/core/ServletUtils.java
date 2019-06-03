package com.blecua84.moneytransfers.core;

import com.blecua84.moneytransfers.core.exceptions.ServletUtilsException;

import javax.servlet.http.HttpServletRequest;

public interface ServletUtils {

    <T> T readBody(HttpServletRequest request, Class<T> targetClass) throws ServletUtilsException;
}
