package com.blecua84.moneytransfers;

import com.blecua84.moneytransfers.server.JettyServer;

public class AppMainRunner {

    private static JettyServer serverInstance;

    public static JettyServer getServerInstance() {
        return serverInstance;
    }

    public static void main(String[] args) throws Exception {
        serverInstance = JettyServer.getInstance();
        serverInstance.start(8080);


        System.out.println("Listening connections...");
    }
}
