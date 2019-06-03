package com.blecua84.moneytransfers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AppMainRunnerTest {

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

    @AfterEach
    void tearDown() {
        AppMainRunner.getJettyServerInstance().stop();
    }

    @Test
    void main_shouldStartANewServerInADefaultPort() throws Exception {
        AppMainRunner.main(new String[]{});

        String url = "http://localhost:8080";
        var httpRequest = HttpRequest.newBuilder(new URI(url))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(INITIAL_HTTP_STATUS, httpResponse.statusCode());
        } catch (IOException e) {
            fail();
        }
    }

}
