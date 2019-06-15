package com.blecua84.moneytransfers.concurrency;

import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TransferExecutor extends Thread {

    private TransferDTO transferDTO;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private HttpResponse<String> result;

    TransferExecutor(TransferDTO transferDTO) {
        this.transferDTO = transferDTO;

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .proxy(ProxySelector.getDefault())
                .build();

        this.objectMapper = new ObjectMapper();
    }

    HttpResponse<String> getResult() {
        return result;
    }

    @Override
    public void run() {
        try {
            result = executeCall();
        } catch (InterruptedException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private HttpResponse<String> executeCall() throws InterruptedException, URISyntaxException, IOException {
        return httpClient.send(this.createHttpRequestPostWithBody(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest createHttpRequestPostWithBody() throws URISyntaxException, JsonProcessingException {
        String url = "http://localhost:8080/transfers";
        return HttpRequest.newBuilder(new URI(url))
                .method("POST", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(transferDTO)))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .build();
    }
}
