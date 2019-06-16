package com.blecua84.moneytransfers.core.impl;

import com.blecua84.moneytransfers.converters.impl.AccountDTOToModelConverter;
import com.blecua84.moneytransfers.converters.impl.AccountToDTOModelConverter;
import com.blecua84.moneytransfers.converters.impl.TransferListToTransferDTOConverter;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
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

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class DefaultContextContainer {

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

    public static void init() {
        log.info("Init context");
        initInstances();
        injectDependencies();
        loadInitialDataInDB();
        log.info("Context initialized");
    }

    public static void start() throws Exception {
        log.info("Start application");
        jettyServerInstance.start(8080);
        log.info("Application already started");
    }

    public static void stop() {
        log.info("Stopping application");
        jettyServerInstance.stop();
        log.info("Application already stopped");
    }

    private static void initInstances() {
        log.info("Initialising components");
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
        log.info("Components already initialised");
    }

    private static void injectDependencies() {
        log.info("Injecting dependencies into components");
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
        log.info("Dependencies already injected into components");
    }

    private static void loadInitialDataInDB() {
        log.info("Loading data into database");
        InputStream accounts = ClassLoader.getSystemClassLoader().getResourceAsStream("load-initial-data.json");
        Account[] accountList;
        try {
            accountList = objectMapperInstance.readValue(accounts, Account[].class);
            for (Account account : accountList) {
                try {
                    accountDAOInstance.saveAccount(account);
                } catch (DataManagerException e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Data already loaded into database");
    }
}
