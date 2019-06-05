package com.blecua84.moneytransfers.server;

import com.blecua84.moneytransfers.router.TransfersServlet;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

@Slf4j
public class JettyServer {

    private static JettyServer instance;

    private Server server;
    private TransfersServlet servlet;

    private JettyServer() {
    }

    public static JettyServer getInstance() {
        if (instance == null) {
            instance = new JettyServer();
        }
        return instance;
    }

    public void setServlet(TransfersServlet servlet) {
        this.servlet = servlet;
    }

    public void start(int port) throws Exception {
        log.info("Init start in port: " + port);

        int maxThreads = 1000;
        int minThreads = 10;
        int idleTimeout = 120;

        this.server = new Server(new QueuedThreadPool(maxThreads, minThreads, idleTimeout));

        ServerConnector connector = new ServerConnector(this.server);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        ServletHolder transfersServletHolder = new ServletHolder(servlet);
        servletHandler.addServletWithMapping(transfersServletHolder, TransfersServlet.URL_PATTERN);

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
