package com.blecua84.moneytransfers.core.impl;

import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.exceptions.ServletUtilsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@Data
@Slf4j
public class DefaultServletUtils implements ServletUtils {

    private static final String GENERIC_ERROR_MESSAGE = "There was an error trying to extract the body from the request.";
    private static DefaultServletUtils instance;

    private ObjectMapper objectMapper;

    private DefaultServletUtils() {
    }

    public static DefaultServletUtils getInstance() {
        if (instance == null) {
            instance = new DefaultServletUtils();
        }

        return instance;
    }

    @Override
    public <T> T readBody(HttpServletRequest request, Class<T> targetClass) throws ServletUtilsException {
        log.info("Init readBody for class: " + targetClass);
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            T readBody = objectMapper.readValue(body, targetClass);
            log.info("End readBody for class: " + targetClass);
            return readBody;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServletUtilsException(GENERIC_ERROR_MESSAGE, e);
        }
    }
}
