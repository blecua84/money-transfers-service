package com.blecua84.moneytransfers.core.impl;

import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.exceptions.ServletUtilsException;
import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultServletUtilsTest {

    private ServletUtils servletUtils;

    @BeforeEach
    void setUp() {
        this.servletUtils = DefaultServletUtils.getInstance();
    }

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(DefaultServletUtils.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        DefaultServletUtils firstInstance = DefaultServletUtils.getInstance();
        DefaultServletUtils secondInstance = DefaultServletUtils.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void readBody_whenItReceivesABodyWithRequestAndTheCorrectType_shouldReturnAValidObjectCreatedWithTheSameData() throws IOException, ServletUtilsException {
        HttpServletRequest request = getMockedRequestWithBodyFromFilePath("src/test/resources/test-files/transfers-input-01.json");

        TransferDTO result = this.servletUtils.readBody(request, TransferDTO.class);

        assertNotNull(result);
        assertEquals(new AccountDTO("010203", "10305070"), result.getFrom());
        assertEquals(new AccountDTO("010203", "12345678"), result.getTo());
        assertEquals("10.50", result.getAmount());
    }

    @Test
    void readBody_whenItReceivesAnEmptyBody_shouldThrowAnException() throws IOException {
        HttpServletRequest request = getMockedRequestWithBodyFromFilePath("src/test/resources/test-files/transfers-input-02.json");

        try {
            this.servletUtils.readBody(request, TransferDTO.class);
            fail();
        } catch (ServletUtilsException e) {
            assertEquals("There was an error trying to extract the body from the request.", e.getMessage());
        }
    }

    @Test
    void readBody_whenItReceivesAnValidBodyButTheTypeIsNotMatching_shouldThrowAnException() throws IOException {
        HttpServletRequest request = getMockedRequestWithBodyFromFilePath("src/test/resources/test-files/transfers-input-01.json");

        try {
            this.servletUtils.readBody(request, AccountDTO.class);
            fail();
        } catch (ServletUtilsException e) {
            assertEquals("There was an error trying to extract the body from the request.", e.getMessage());
        }
    }

    private HttpServletRequest getMockedRequestWithBodyFromFilePath(String filePath) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        BufferedReader bufferedReader = getBufferedReaderByFilePath(filePath);
        when(request.getReader()).thenReturn(bufferedReader);
        return request;
    }

    private BufferedReader getBufferedReaderByFilePath(String filePath) throws IOException {
        StringReader reader = new StringReader(readFromInputStream(filePath));
        return new BufferedReader(reader);
    }

    private String readFromInputStream(String filePath) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        InputStream inputStream = new FileInputStream(new File(filePath));
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = buffer.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
