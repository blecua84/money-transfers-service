package com.blecua84.moneytransfers;

import com.blecua84.moneytransfers.converters.impl.AccountDTOToModelConverter;
import com.blecua84.moneytransfers.converters.impl.AccountToDTOModelConverter;
import com.blecua84.moneytransfers.converters.impl.TransferListToTransferDTOConverter;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
import com.blecua84.moneytransfers.core.impl.DefaultServletUtils;
import com.blecua84.moneytransfers.persistence.DataManager;
import com.blecua84.moneytransfers.persistence.daos.impl.DefaultAccountDAO;
import com.blecua84.moneytransfers.persistence.daos.impl.DefaultTransfersDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.persistence.impl.DefaultDataManager;
import com.blecua84.moneytransfers.router.TransfersServlet;
import com.blecua84.moneytransfers.server.JettyServer;
import com.blecua84.moneytransfers.services.impl.DefaultTransfersService;
import com.blecua84.moneytransfers.services.models.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
public class AppMainRunner {

    private static JettyServer jettyServerInstance;
    private static TransfersServlet transfersServletInstance;
    private static DefaultServletUtils servletUtilsInstance;

    private static TransfersDTOToModelConverter transfersDTOToModelConverterInstance;
    private static AccountDTOToModelConverter accountDTOToModelConverterInstance;
    private static TransferListToTransferDTOConverter transferListToTransferDTOConverter;
    private static AccountToDTOModelConverter accountToDTOModelConverter;

    private static DefaultTransfersService transfersServiceInstance;
    private static DataManager dataManagerInstance;
    private static DefaultAccountDAO accountDAOInstance;
    private static DefaultTransfersDAO transfersDAOInstance;

    private static ObjectMapper objectMapperInstance;

    public static JettyServer getJettyServerInstance() {
        return jettyServerInstance;
    }

    public static void main(String[] args) throws Exception {
        printHeader();
        Instant start = Instant.now();

        initComponents();
        jettyServerInstance.start(8080);

        printTimeElapsed(start);
    }


    private static void initComponents() {
        initInstances();
        injectDependencies();

        try {
            injectDataInDB();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void printTimeElapsed(Instant startTime) {
        Duration timeElapsed = Duration.between(startTime, Instant.now());
        log.debug("Time elapsed: " + timeElapsed.toMillis() / 1000f + " seconds");
    }

    private static void printHeader() {
        InputStream headerFile = ClassLoader.getSystemClassLoader().getResourceAsStream("header.txt");
        assert headerFile != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(headerFile));
        log.info(readAllLines(reader));
    }

    private static String readAllLines(BufferedReader reader) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    private static void injectDataInDB() throws IOException {
        InputStream accounts = ClassLoader.getSystemClassLoader().getResourceAsStream("load-initial-data.json");
        Account[] accountList = objectMapperInstance.readValue(accounts, Account[].class);

        for (Account account : accountList) {
            try {
                accountDAOInstance.saveAccount(account);
            } catch (DataManagerException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private static void initInstances() {
        transfersServiceInstance = DefaultTransfersService.getInstance();
        transfersDTOToModelConverterInstance = TransfersDTOToModelConverter.getInstance();
        accountDTOToModelConverterInstance = AccountDTOToModelConverter.getInstance();
        transferListToTransferDTOConverter = TransferListToTransferDTOConverter.getInstance();
        accountToDTOModelConverter = AccountToDTOModelConverter.getInstance();
        servletUtilsInstance = DefaultServletUtils.getInstance();
        transfersServletInstance = TransfersServlet.getInstance();
        jettyServerInstance = JettyServer.getInstance();
        dataManagerInstance = DefaultDataManager.getInstance();
        accountDAOInstance = DefaultAccountDAO.getInstance();
        transfersDAOInstance = DefaultTransfersDAO.getInstance();
        objectMapperInstance = new ObjectMapper();
    }

    private static void injectDependencies() {
        transfersServletInstance.setTransfersService(transfersServiceInstance);
        transfersServletInstance.setTransfersDTOToModelConverter(transfersDTOToModelConverterInstance);
        transfersServletInstance.setServletUtils(servletUtilsInstance);
        transfersServletInstance.setTransferListToTransferDTOConverter(transferListToTransferDTOConverter);
        transfersServletInstance.setObjectMapper(objectMapperInstance);
        servletUtilsInstance.setObjectMapper(objectMapperInstance);
        jettyServerInstance.setServlet(transfersServletInstance);
        transfersDTOToModelConverterInstance.setAccountDTOToModelConverter(accountDTOToModelConverterInstance);
        transferListToTransferDTOConverter.setAccountToDTOModelConverter(accountToDTOModelConverter);
        accountDAOInstance.setDataManager(dataManagerInstance);
        transfersDAOInstance.setDataManager(dataManagerInstance);
        transfersDAOInstance.setAccountDAO(accountDAOInstance);
        transfersServiceInstance.setTransfersDAO(transfersDAOInstance);
        transfersServiceInstance.setAccountDAO(accountDAOInstance);
    }
}
