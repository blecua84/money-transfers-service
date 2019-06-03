package com.blecua84.moneytransfers.router;

import com.blecua84.moneytransfers.converters.exceptions.ConverterException;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.exceptions.ServletUtilsException;
import com.blecua84.moneytransfers.router.models.ResultDTO;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Transfer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
@WebServlet(name = "transfersServlet", urlPatterns = TransfersServlet.URL_PATTERN, asyncSupported = true)
public class TransfersServlet extends HttpServlet {

    public static final String URL_PATTERN = "/transfers";
    public static final String TRANSFER_CREATED_OK_MESSAGE = "Operation successfully executed";

    private static TransfersServlet instance;

    private TransfersService transfersService;
    private TransfersDTOToModelConverter transfersDTOToModelConverter;
    private ServletUtils servletUtils;
    private ObjectMapper objectMapper;

    private TransfersServlet() {
        this.objectMapper = new ObjectMapper();
    }

    public static TransfersServlet getInstance() {
        if (instance == null) {
            instance = new TransfersServlet();
        }
        return instance;
    }

    public void setTransfersService(TransfersService transfersService) {
        this.transfersService = transfersService;
    }

    public void setTransfersDTOToModelConverter(TransfersDTOToModelConverter transfersDTOToModelConverter) {
        this.transfersDTOToModelConverter = transfersDTOToModelConverter;
    }

    public void setServletUtils(ServletUtils servletUtils) {
        this.servletUtils = servletUtils;
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        log.info("Init doPost");
        CompletableFuture<ResultDTO> future = supplyAsync(() -> createTransferFromRequest(req));

        executeCompletableFuture(future, resp);
        log.info("End doPost");
    }

    private void executeCompletableFuture(CompletableFuture<ResultDTO> future, HttpServletResponse resp) {
        try {
            writeResultInResponse(future.get(), resp);
        } catch (InterruptedException | IOException | ExecutionException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private ResultDTO createTransferFromRequest(HttpServletRequest req) {
        log.info("Preparing data to call the service");
        Integer status = HttpServletResponse.SC_OK;
        String message = TransfersServlet.TRANSFER_CREATED_OK_MESSAGE;

        try {
            TransferDTO dataFromBody = this.servletUtils.readBody(req, TransferDTO.class);
            log.debug("TransferDTO: " + dataFromBody.toString());

            Transfer transfer = this.transfersDTOToModelConverter.convert(dataFromBody);
            log.debug("Transfer: " + transfer.toString());

            log.debug("Executing service...");
            transfersService.create(transfer);
            log.debug("Service successfully executed!");
        } catch (ServletUtilsException | ConverterException | TransfersException e) {
            log.error("There was an error", e);
            status = HttpServletResponse.SC_BAD_REQUEST;
            message = e.getMessage();
        }

        log.info("Operation status: " + status);
        log.info("Operation message: " + message);
        return new ResultDTO(status, message);
    }

    private void writeResultInResponse(ResultDTO resultDTO, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(resultDTO.getStatus());

        this.objectMapper.writeValue(out, resultDTO);
        out.flush();
    }
}
