package com.blecua84.moneytransfers.server;

import com.blecua84.moneytransfers.converters.impl.AccountDTOToModelConverter;
import com.blecua84.moneytransfers.converters.impl.TransfersDTOToModelConverter;
import com.blecua84.moneytransfers.core.ServletUtils;
import com.blecua84.moneytransfers.core.impl.DefaultServletUtils;
import com.blecua84.moneytransfers.router.TransfersServlet;
import com.blecua84.moneytransfers.services.TransfersService;
import com.blecua84.moneytransfers.services.impl.DefaultTransfersService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

@Slf4j
public class JettyServer {

    private static JettyServer instance;
    private Server server;

    private JettyServer() {
        this.server = new Server();
    }

    public static JettyServer getInstance() {
        if (instance == null) {
            instance = new JettyServer();
        }
        return instance;
    }

    public void start(int port) throws Exception {
        log.info("Init start in port: " + port);
        this.server = new Server();
        ServerConnector connector = new ServerConnector(this.server);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        TransfersService transfersService = new DefaultTransfersService();

        TransfersDTOToModelConverter converter = TransfersDTOToModelConverter.getInstance();
        converter.setAccountDTOToModelConverter(AccountDTOToModelConverter.getInstance());

        ServletUtils servletUtils = DefaultServletUtils.getInstance();

        TransfersServlet servlet = TransfersServlet.getInstance();
        servlet.setTransfersService(transfersService);
        servlet.setTransfersDTOToModelConverter(converter);
        servlet.setServletUtils(servletUtils);
        ServletHolder transfersServletHolder = new ServletHolder(servlet);
        servletHandler.addServletWithMapping(transfersServletHolder, "/transfers");

        server.start();

        log.info("Server initialised in port: " + port);
    }

    public void stop() {
        log.info("Init stop");
        try {
            this.server.stop();
            log.info("The server was shutdown properly.");
        } catch (Exception e) {
            log.error("The system could not stop server.", e);
        }
        log.info("Init stop");
    }
}
