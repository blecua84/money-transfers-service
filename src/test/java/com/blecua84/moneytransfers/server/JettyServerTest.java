package com.blecua84.moneytransfers.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JettyServerTest {

    private static final int INITIAL_HTTP_STATUS = 404;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .proxy(ProxySelector.getDefault())
                .build();
    }

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(JettyServer.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        JettyServer firstInstance = JettyServer.getInstance();
        JettyServer secondInstance = JettyServer.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void start_whenItReceivesAPort_shouldStartTheServerInThatPort() throws Exception {
        JettyServer server = JettyServer.getInstance();
        server.start(8090);

        String url = "http://localhost:8090";
        var httpRequest = HttpRequest.newBuilder(new URI(url))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(INITIAL_HTTP_STATUS, httpResponse.statusCode());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void stop_shouldCloseConnection() throws Exception {
        JettyServer server = JettyServer.getInstance();
        server.stop();

        String url = "http://localhost:8090";
        var httpRequest = HttpRequest.newBuilder(new URI(url))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .build();

        try {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            fail();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
