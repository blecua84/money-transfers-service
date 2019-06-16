package com.blecua84.moneytransfers;

import com.blecua84.moneytransfers.persistence.daos.impl.DefaultAccountDAO;
import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import com.blecua84.moneytransfers.router.models.AccountDTO;
import com.blecua84.moneytransfers.router.models.ResultDTO;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.blecua84.moneytransfers.services.models.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AppMainRunnerTest {

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

        AppMainRunner.stop();
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithBody_shouldCallToTheService()
            throws URISyntaxException, InterruptedException, FileNotFoundException, DataManagerException {
        DefaultAccountDAO accountDAO = DefaultAccountDAO.getInstance();
        accountDAO.saveAccount(new Account("090129", "12340013", new BigDecimal(1000)));
        accountDAO.saveAccount(new Account("090129", "12340014", new BigDecimal(2000)));
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-16.json");

        executeTest(httpRequest, 200, "Operation successfully executed");
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithoutBody_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-02.json");

        executeTest(httpRequest, 400,
                "There was an error trying to extract the body from the request.");
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithAmountNotValid_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-03.json");

        executeTest(httpRequest, 400, TransferDTO.AMOUNT_ERROR_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithAmountNull_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-05.json");

        executeTest(httpRequest, 400, TransferDTO.AMOUNT_ERROR_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithAmountEmpty_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-06.json");

        executeTest(httpRequest, 400, TransferDTO.AMOUNT_ERROR_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithFromAccountNull_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-04.json");

        executeTest(httpRequest, 400, TransferDTO.FROM_ERROR_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithFromAccountSortCodeNotValid_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-07.json");

        executeTest(httpRequest, 400, AccountDTO.SORT_CODE_NOT_VALID_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithFromAccountSortCodeNull_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-08.json");

        executeTest(httpRequest, 400, AccountDTO.SORT_CODE_NOT_VALID_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithFromAccountAccountNumberNotValid_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-09.json");

        executeTest(httpRequest, 400, AccountDTO.ACCOUNT_NUMBER_NOT_VALID_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithFromAccountAccountNumberNull_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-10.json");

        executeTest(httpRequest, 400, AccountDTO.ACCOUNT_NUMBER_NOT_VALID_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithToAccountNull_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-11.json");

        executeTest(httpRequest, 400, TransferDTO.TO_ERROR_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithToAccountSortCodeNotValid_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-12.json");

        executeTest(httpRequest, 400, AccountDTO.SORT_CODE_NOT_VALID_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithToAccountAccountNumberNotValid_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-13.json");

        executeTest(httpRequest, 400, AccountDTO.ACCOUNT_NUMBER_NOT_VALID_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithToAccountAccountNumberNull_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-14.json");

        executeTest(httpRequest, 400, AccountDTO.ACCOUNT_NUMBER_NOT_VALID_MSG);
    }

    @Test
    void transfersEndpoint_whenItIsInvokedWithToAccountSortCodeNull_shouldReturnWithABadRequest()
            throws URISyntaxException, InterruptedException, FileNotFoundException {
        HttpRequest httpRequest = createHttpRequestPostWithBody(
                "src/test/resources/test-files/transfers-input-15.json");

        executeTest(httpRequest, 400, AccountDTO.SORT_CODE_NOT_VALID_MSG);
    }

    private void executeTest(HttpRequest httpRequest, int expectedStatus, String expectedMessage)
            throws InterruptedException {
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ResultDTO resultDTO = getOutputFromResponse(httpResponse);

            assertEquals(expectedStatus, httpResponse.statusCode());
            assertNotNull(resultDTO);
            assertEquals(expectedMessage, resultDTO.getMessage());
        } catch (IOException e) {
            fail();
        }
    }

    private HttpRequest createHttpRequestPostWithBody(String filePath) throws URISyntaxException, FileNotFoundException {
        String url = "http://localhost:8080/transfers";
        return HttpRequest.newBuilder(new URI(url))
                .method("POST", HttpRequest.BodyPublishers.ofFile(Paths.get(filePath)))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .build();
    }

    private ResultDTO getOutputFromResponse(HttpResponse response) throws IOException {
        return objectMapper.readValue((String) response.body(), ResultDTO.class);
    }
}
