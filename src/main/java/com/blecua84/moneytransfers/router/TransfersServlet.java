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
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
@WebServlet(name = "transfersServlet", urlPatterns = TransfersServlet.URL_PATTERN, asyncSupported = true)
public class TransfersServlet extends HttpServlet {

    public static final String URL_PATTERN = "/transfers";
    private static final String TRANSFER_CREATED_OK_MESSAGE = "Operation successfully executed";

    private static TransfersServlet instance;
    private static Validator validator;

    private TransfersService transfersService;
    private TransfersDTOToModelConverter transfersDTOToModelConverter;
    private ServletUtils servletUtils;
    private ObjectMapper objectMapper;

    private TransfersServlet() {
        this.objectMapper = new ObjectMapper();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
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
        ResultDTO resultDTO;

        try {
            TransferDTO dataFromBody = this.servletUtils.readBody(req, TransferDTO.class);
            log.debug("TransferDTO: " + dataFromBody.toString());

            Set<ConstraintViolation<TransferDTO>> constraintViolationSet = validator.validate(dataFromBody);

            if (thereIsAnyValidationError(constraintViolationSet)) {
                Transfer transfer = this.transfersDTOToModelConverter.convert(dataFromBody);
                log.debug("Transfer: " + transfer.toString());

                log.debug("Executing service...");
                transfersService.create(transfer);
                log.debug("Service successfully executed!");

                resultDTO = createResultDTO(HttpServletResponse.SC_OK, TransfersServlet.TRANSFER_CREATED_OK_MESSAGE);
            } else {
                resultDTO = createResultDTO(HttpServletResponse.SC_BAD_REQUEST,
                        extractFirstErrorMessageFromViolationSet(constraintViolationSet));
                log.error("There was a validation error: " + resultDTO.getMessage());
            }
        } catch (ServletUtilsException | ConverterException | TransfersException e) {
            log.error("There was an error", e);
            resultDTO = createResultDTO(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

        log.debug("Operation result: " + resultDTO);
        return resultDTO;
    }

    private String extractFirstErrorMessageFromViolationSet(Set<ConstraintViolation<TransferDTO>> constraintViolationSet) {
        return ((ConstraintViolationImpl) constraintViolationSet.toArray()[0]).getMessage();
    }

    private ResultDTO createResultDTO(int status, String message) {
        return new ResultDTO(status, message);
    }

    private boolean thereIsAnyValidationError(Set<ConstraintViolation<TransferDTO>> constraintViolationSet) {
        return constraintViolationSet.size() == 0;
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
