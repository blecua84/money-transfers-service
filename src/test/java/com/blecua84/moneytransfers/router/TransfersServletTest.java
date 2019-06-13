package com.blecua84.moneytransfers.router;

import com.blecua84.moneytransfers.converters.exceptions.ConverterException;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.exceptions.ServletUtilsException;
import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Account;
import com.blecua84.moneytransfers.services.models.Transfer;
import com.blecua84.moneytransfers.test.utils.TestHttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TransfersServletTest {

    private TransfersService transfersService;
    private TransfersDTOToModelConverter transfersDTOToModelConverter;
    private TransfersServlet transfersServlet;
    private ServletUtils servletUtils;

    @BeforeEach
    void setUp() {
        this.transfersDTOToModelConverter = mock(TransfersDTOToModelConverter.class);
        this.transfersService = mock(TransfersService.class);
        this.servletUtils = mock(ServletUtils.class);

        this.transfersServlet = TransfersServlet.getInstance();
        this.transfersServlet.setTransfersService(this.transfersService);
        this.transfersServlet.setTransfersDTOToModelConverter(this.transfersDTOToModelConverter);
        this.transfersServlet.setServletUtils(this.servletUtils);
        this.transfersServlet.setObjectMapper(mock(ObjectMapper.class));
    }

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(TransfersServlet.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        TransfersServlet firstInstance = TransfersServlet.getInstance();
        TransfersServlet secondInstance = TransfersServlet.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void doPost_whenItIsInvoked_shouldTranslateTheInputToServiceModel() throws IOException, ServletUtilsException, ConverterException {
        TransferDTO inputTransferToDo = new TransferDTO(
                new AccountDTO("010203", "43546576"),
                new AccountDTO("010203", "12345678"),
                "150");
        Transfer transferToDo = new Transfer(
                new Account("010203", "43546576", new BigDecimal(250.45)),
                new Account("010203", "12345678", new BigDecimal(40)),
                new BigDecimal(150));
        when(this.transfersDTOToModelConverter.convert(eq(inputTransferToDo))).thenReturn(transferToDo);
        HttpServletRequest mockRequest = new TestHttpServletRequest();
        when(this.servletUtils.readBody(eq(mockRequest), eq(TransferDTO.class))).thenReturn(inputTransferToDo);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        when(mockResponse.getWriter()).thenReturn(mock(PrintWriter.class));

        this.transfersServlet.doPost(mockRequest, mockResponse);

        verify(this.transfersDTOToModelConverter).convert(eq(inputTransferToDo));
    }

    @Test
    void doPost_whenItReceivesAValidRequest_shouldCallToTheService() throws TransfersException, ServletUtilsException, IOException, ConverterException {
        TransferDTO inputTransferToDo = new TransferDTO(
                new AccountDTO("010203", "43546576"),
                new AccountDTO("010203", "12345678"),
                "150");
        Transfer transferToDo = new Transfer(
                new Account("010203", "43546576", new BigDecimal(250.45)),
                new Account("010203", "12345678", new BigDecimal(40)),
                new BigDecimal(150));
        when(this.transfersDTOToModelConverter.convert(eq(inputTransferToDo))).thenReturn(transferToDo);
        HttpServletRequest mockRequest = new TestHttpServletRequest();
        when(this.servletUtils.readBody(eq(mockRequest), eq(TransferDTO.class))).thenReturn(inputTransferToDo);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        when(mockResponse.getWriter()).thenReturn(mock(PrintWriter.class));

        this.transfersServlet.doPost(mockRequest, mockResponse);

        verify(this.transfersService).create(eq(transferToDo));
    }

    @Test
    void doGet_whenItReceivesAValidRequest_shouldCallToTheService() throws TransfersException {
        when(this.transfersService.getTransfers()).thenReturn(mock(List.class));
        HttpServletRequest mockRequest = new TestHttpServletRequest();
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        this.transfersServlet.doGet(mockRequest, mockResponse);

        verify(this.transfersService).getTransfers();
    }
}
