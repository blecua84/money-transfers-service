package com.blecua84.moneytransfers.router;

import com.blecua84.moneytransfers.converters.exceptions.ConverterException;
import com.blecua84.moneytransfers.converters.impl.TransferListToTransferDTOConverter;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.exceptions.ServletUtilsException;
import com.blecua84.moneytransfers.core.impl.BaseController;
import com.blecua84.moneytransfers.router.models.ResultDTO;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Transfer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@WebServlet(name = "transfersServlet", urlPatterns = TransfersServlet.URL_PATTERN, asyncSupported = true)
public class TransfersServlet extends BaseController {

    public static final String URL_PATTERN = "/transfers";
    private static final String TRANSFER_CREATED_OK_MESSAGE = "Operation successfully executed";

    private static TransfersServlet instance;
    private static Validator validator;

    private TransfersService transfersService;
    private TransfersDTOToModelConverter transfersDTOToModelConverter;
    private TransferListToTransferDTOConverter transferListToTransferDTOConverter;
    private ServletUtils servletUtils;

    private TransfersServlet() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static TransfersServlet getInstance() {
        if (instance == null) {
            instance = new TransfersServlet();
        }
        return instance;
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        log.info("Init doPost");
        CompletableFuture<ResultDTO> future = supplyAsync(() -> createTransfer(req));

        executeCompletableFuture(future, resp);
        log.info("End doPost");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.info("Init doGet");
        CompletableFuture<ResultDTO> future = supplyAsync(this::getTransfers);

        executeCompletableFuture(future, resp);
        log.info("End doGet");
    }

    private ResultDTO createTransfer(HttpServletRequest req) {
        log.info("Preparing data to call the service");
        ResultDTO resultDTO;

        try {
            TransferDTO dataFromBody = this.servletUtils.readBody(req, TransferDTO.class);
            log.debug("TransferDTO: " + dataFromBody.toString());

            Set<ConstraintViolation<TransferDTO>> constraintViolationSet = validator.validate(dataFromBody);

            if (anyValidationError(constraintViolationSet)) {
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

    private ResultDTO<List<TransferDTO>> getTransfers() {
        log.info("Init getTransfers");
        ResultDTO<List<TransferDTO>> result;

        try {
            List<Transfer> transfers = this.transfersService.getTransfers();

            result = createResultDTO(
                    HttpServletResponse.SC_OK,
                    TransfersServlet.TRANSFER_CREATED_OK_MESSAGE,
                    this.transferListToTransferDTOConverter.convert(transfers));
        } catch (TransfersException e) {
            log.error(e.getMessage(), e);
            result = createResultDTO(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }

        log.info("End getTransfers");
        return result;
    }
}
