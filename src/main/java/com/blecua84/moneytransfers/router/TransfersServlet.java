package com.blecua84.moneytransfers.router;

import com.blecua84.moneytransfers.converters.exceptions.ConverterException;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.exceptions.ServletUtilsException;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.exceptions.TransfersException;
import com.blecua84.moneytransfers.services.models.Transfer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@WebServlet(name = "transfersServlet", urlPatterns = "/transfers", asyncSupported = true)
public class TransfersServlet extends HttpServlet {

    private static TransfersServlet instance;

    private TransfersService transfersService;
    private TransfersDTOToModelConverter transfersDTOToModelConverter;
    private ServletUtils servletUtils;

    private TransfersServlet() {
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
        CompletableFuture<Integer> future = supplyAsync(() -> createTransferFromRequest(req));

        Integer status;
        try {
            status = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
    }

    private Integer createTransferFromRequest(HttpServletRequest req) {
        Integer status = HttpServletResponse.SC_OK;
        try {
            TransferDTO dataFromBody = this.servletUtils.readBody(req, TransferDTO.class);
            Transfer transfer = this.transfersDTOToModelConverter.convert(dataFromBody);

            transfersService.create(transfer);
        } catch (ServletUtilsException | ConverterException | TransfersException e) {
            e.printStackTrace();
            status = HttpServletResponse.SC_BAD_REQUEST;
        }

        return status;
    }
}
