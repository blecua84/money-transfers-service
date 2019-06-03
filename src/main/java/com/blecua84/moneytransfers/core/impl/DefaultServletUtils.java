package com.blecua84.moneytransfers.core.impl;

import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.exceptions.ServletUtilsException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

public class DefaultServletUtils implements ServletUtils {

    private static final String GENERIC_ERROR_MESSAGE = "There was an error trying to extract the body from the request.";
    private static DefaultServletUtils instance;

    private final ObjectMapper objectMapper;

    private DefaultServletUtils() {
        this.objectMapper = new ObjectMapper();
    }

    public static DefaultServletUtils getInstance() {
        if (instance == null) {
            instance = new DefaultServletUtils();
        }

        return instance;
    }

    @Override
    public <T> T readBody(HttpServletRequest request, Class<T> targetClass) throws ServletUtilsException {
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            return objectMapper.readValue(body, targetClass);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletUtilsException(GENERIC_ERROR_MESSAGE);
        }
    }
}
