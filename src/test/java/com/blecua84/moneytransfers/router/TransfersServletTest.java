package com.blecua84.moneytransfers.router;

import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransfersServletTest {

    @Test
    void doPost_shouldExist() throws ClassNotFoundException, NoSuchMethodException {
        assertNotNull(Class.forName("com.blecua84.moneytransfers.router.TransfersServlet").getMethod(
                "doPost",
                HttpServletRequest.class, HttpServletResponse.class));
    }

}
