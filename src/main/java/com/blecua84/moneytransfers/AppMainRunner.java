package com.blecua84.moneytransfers;

import com.blecua84.moneytransfers.server.JettyServer;

public class AppMainRunner {

    public static void main(String[] args) throws Exception {
        JettyServer serverInstance = JettyServer.getInstance();
        serverInstance.start(8080);


        System.out.println("Listening connections...");
    }
}
