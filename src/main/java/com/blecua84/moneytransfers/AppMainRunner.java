package com.blecua84.moneytransfers;

import com.blecua84.moneytransfers.server.JettyServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppMainRunner {

    private static JettyServer serverInstance;

    public static JettyServer getServerInstance() {
        return serverInstance;
    }

    public static void main(String[] args) throws Exception {
        log.info("Start main");
        serverInstance = JettyServer.getInstance();
        serverInstance.start(8080);
        log.debug("Listening connections...");
    }
}
