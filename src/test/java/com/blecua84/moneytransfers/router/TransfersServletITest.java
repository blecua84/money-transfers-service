package com.blecua84.moneytransfers.router;

import com.blecua84.moneytransfers.AppMainRunner;
import com.blecua84.moneytransfers.router.models.ResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.jupiter.api.Assertions.*;

class TransfersServletITest {

    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .proxy(ProxySelector.getDefault())
                .build();

        this.objectMapper = new ObjectMapper();

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
            ResultDTO resultDTO = getOutputFromResponse(httpResponse);

            assertEquals(200, httpResponse.statusCode());
            assertNotNull(resultDTO);
            assertEquals("Operation successfully executed", resultDTO.getMessage());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithoutBody_shouldReturnWithABadRequest() throws URISyntaxException, InterruptedException, FileNotFoundException {
        String url = "http://localhost:8080/transfers";
        var httpRequest = HttpRequest.newBuilder(new URI(url))
                .method("POST", HttpRequest.BodyPublishers.ofFile(Paths.get("src/test/resources/test-files/transfers-input-02.json")))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ResultDTO resultDTO = getOutputFromResponse(httpResponse);

            assertEquals(400, httpResponse.statusCode());
            assertNotNull(resultDTO);
            assertEquals("There was an error trying to extract the body from the request.", resultDTO.getMessage());
        } catch (IOException e) {
            fail();
        }
    }

    private ResultDTO getOutputFromResponse(HttpResponse response) throws IOException {
        return objectMapper.readValue((String) response.body(), ResultDTO.class);
    }
}
