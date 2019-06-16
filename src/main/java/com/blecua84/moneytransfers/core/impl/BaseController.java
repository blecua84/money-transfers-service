package com.blecua84.moneytransfers.core.impl;

import com.blecua84.moneytransfers.router.models.ResultDTO;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public abstract class BaseController<T> extends HttpServlet {

    private ObjectMapper objectMapper;

    protected abstract ResultDTO<T> processPostMethod(HttpServletRequest request);

    protected abstract ResultDTO<List<T>> processGetMethod();

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        log.info("Init doPost");
        CompletableFuture<ResultDTO> future = supplyAsync(() -> processPostMethod(req));

        executeCompletableFuture(future, resp);
        log.info("End doPost");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.info("Init doGet");
        CompletableFuture<ResultDTO> future = supplyAsync(this::processGetMethod);

        executeCompletableFuture(future, resp);
        log.info("End doGet");
    }

    protected String extractFirstErrorMessageFromViolationSet(
            Set<ConstraintViolation<TransferDTO>> constraintViolationSet) {
        return ((ConstraintViolationImpl) constraintViolationSet.toArray()[0]).getMessage();
    }

    protected boolean anyValidationError(Set<ConstraintViolation<TransferDTO>> constraintViolationSet) {
        return constraintViolationSet.size() == 0;
    }

    protected <T> ResultDTO<T> createResultDTO(int status, String message, T data) {
        return new ResultDTO<>(status, message, data);
    }

    protected <T> ResultDTO<T> createResultDTO(int status, String message) {
        return new ResultDTO<>(status, message);
    }

    private void executeCompletableFuture(CompletableFuture<ResultDTO> future, HttpServletResponse resp) {
        try {
            writeResultInResponse(future.get(), resp);
        } catch (InterruptedException | IOException | ExecutionException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void writeResultInResponse(ResultDTO resultDTO, HttpServletResponse response)
            throws IOException {
        PrintWriter out = response.getWriter();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(resultDTO.getStatus());

        this.objectMapper.writeValue(out, resultDTO);
        out.flush();
    }
}
