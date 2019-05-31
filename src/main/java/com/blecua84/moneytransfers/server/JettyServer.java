package com.blecua84.moneytransfers.server;

import com.blecua84.moneytransfers.router.TransfersServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

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
        this.server = new Server();
        ServerConnector connector = new ServerConnector(this.server);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        servletHandler.addServletWithMapping(TransfersServlet.class, "/transfers");
        server.start();
    }
}
