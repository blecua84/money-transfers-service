package com.blecua84.moneytransfers.router;

import com.blecua84.moneytransfers.AppMainRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TransfersServletITest {

    private HttpClient httpClient;

    @BeforeEach
    void setUp() throws Exception {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .proxy(ProxySelector.getDefault())
                .build();

        AppMainRunner.main(new String[]{});
    }

    @AfterEach
    void tearDown() {
        this.httpClient = null;

        AppMainRunner.getJettyServerInstance().stop();
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithBody_shouldCallToTheService() throws URISyntaxException, InterruptedException, FileNotFoundException {
        String url = "http://localhost:8080/transfers";
        var httpRequest = HttpRequest.newBuilder(new URI(url))
                .method("POST", HttpRequest.BodyPublishers.ofFile(Paths.get("src/test/resources/test-files/transfers-input-01.json")))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, httpResponse.statusCode());
        } catch (IOException e) {
            fail();
        }
    }
}
