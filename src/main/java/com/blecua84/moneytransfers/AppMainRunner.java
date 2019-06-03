package com.blecua84.moneytransfers;

import com.blecua84.moneytransfers.converters.impl.AccountDTOToModelConverter;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.impl.DefaultServletUtils;
import com.blecua84.moneytransfers.router.TransfersServlet;
import com.blecua84.moneytransfers.server.JettyServer;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.impl.DefaultTransfersService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppMainRunner {

    private static JettyServer jettyServerInstance;
    private static TransfersServlet transfersServletInstance;
    private static ServletUtils servletUtilsInstance;

    private static TransfersDTOToModelConverter transfersDTOToModelConverterInstance;
    private static AccountDTOToModelConverter accountDTOToModelConverterInstance;

    private static TransfersService transfersServiceInstance;

    public static JettyServer getJettyServerInstance() {
        return jettyServerInstance;
    }

    public static void main(String[] args) throws Exception {
        log.info("Start main");

        initComponents();

        jettyServerInstance.start(8080);
        log.debug("Listening connections...");
    }

    private static void initComponents() {
        initInstances();
        injectDependencies();
    }

    private static void initInstances() {
        transfersServiceInstance = DefaultTransfersService.getInstance();
        transfersDTOToModelConverterInstance = TransfersDTOToModelConverter.getInstance();
        accountDTOToModelConverterInstance = AccountDTOToModelConverter.getInstance();
        servletUtilsInstance = DefaultServletUtils.getInstance();
        transfersServletInstance = TransfersServlet.getInstance();
        jettyServerInstance = JettyServer.getInstance();
    }

    private static void injectDependencies() {
        transfersServletInstance.setTransfersService(transfersServiceInstance);
        transfersServletInstance.setTransfersDTOToModelConverter(transfersDTOToModelConverterInstance);
        transfersServletInstance.setServletUtils(servletUtilsInstance);
        jettyServerInstance.setServlet(transfersServletInstance);
        transfersDTOToModelConverterInstance.setAccountDTOToModelConverter(accountDTOToModelConverterInstance);
    }
}
