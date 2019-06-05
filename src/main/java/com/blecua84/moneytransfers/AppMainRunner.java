package com.blecua84.moneytransfers;

import com.blecua84.moneytransfers.converters.impl.AccountDTOToModelConverter;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.impl.DefaultServletUtils;
import com.blecua84.moneytransfers.persistence.DataManager;
import com.blecua84.moneytransfers.persistence.daos.impl.DefaultAccountDAO;
import com.blecua84.moneytransfers.persistence.daos.impl.DefaultTransfersDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.persistence.impl.DefaultDataManager;
import com.blecua84.moneytransfers.router.TransfersServlet;
import com.blecua84.moneytransfers.server.JettyServer;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.impl.DefaultTransfersService;
import com.blecua84.moneytransfers.services.models.Account;
import com.blecua84.moneytransfers.services.models.Transfer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppMainRunner {

    private static JettyServer jettyServerInstance;
    private static TransfersServlet transfersServletInstance;
    private static ServletUtils servletUtilsInstance;

    private static TransfersDTOToModelConverter transfersDTOToModelConverterInstance;
    private static AccountDTOToModelConverter accountDTOToModelConverterInstance;

    private static TransfersService transfersServiceInstance;
    private static DataManager dataManagerInstance;
    private static DefaultAccountDAO accountDAOInstance;
    private static DefaultTransfersDAO transfersDAOInstance;

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

        injectDataInDB();
    }

    private static void injectDataInDB() {
        Account account1 = new Account("010203", "12345678", 5000.00F);
        Account account2 = new Account("010203", "12345680", 2500.00F);

        log.info("Persisting accounts...");
        log.info("Account 1: " + account1.toString());
        log.info("Account 2: " + account2.toString());

        try {
            accountDAOInstance.saveAccount(account1);
            accountDAOInstance.saveAccount(account2);

            Transfer newTransfer = new Transfer(account1, account2, 100);
            transfersDAOInstance.saveTransfers(newTransfer);

            for (Transfer transfer : transfersDAOInstance.getTransfers()) {
                log.info("New Transfer found: " + transfer.toString());
            }
        } catch (DataManagerException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void initInstances() {
        transfersServiceInstance = DefaultTransfersService.getInstance();
        transfersDTOToModelConverterInstance = TransfersDTOToModelConverter.getInstance();
        accountDTOToModelConverterInstance = AccountDTOToModelConverter.getInstance();
        servletUtilsInstance = DefaultServletUtils.getInstance();
        transfersServletInstance = TransfersServlet.getInstance();
        jettyServerInstance = JettyServer.getInstance();
        dataManagerInstance = DefaultDataManager.getInstance();
        accountDAOInstance = DefaultAccountDAO.getInstance();
        transfersDAOInstance = DefaultTransfersDAO.getInstance();
    }

    private static void injectDependencies() {
        transfersServletInstance.setTransfersService(transfersServiceInstance);
        transfersServletInstance.setTransfersDTOToModelConverter(transfersDTOToModelConverterInstance);
        transfersServletInstance.setServletUtils(servletUtilsInstance);
        jettyServerInstance.setServlet(transfersServletInstance);
        transfersDTOToModelConverterInstance.setAccountDTOToModelConverter(accountDTOToModelConverterInstance);
        accountDAOInstance.setDataManager(dataManagerInstance);
        transfersDAOInstance.setDataManager(dataManagerInstance);
        transfersDAOInstance.setAccountDAO(accountDAOInstance);
    }
}
