package com.blecua84.moneytransfers.concurrency;

import com.blecua84.moneytransfers.AppMainRunner;
import com.blecua84.moneytransfers.router.models.TransferDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrencyRunnerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        this.objectMapper = new ObjectMapper();

        AppMainRunner.main(new String[]{});
    }

    @AfterEach
    void tearDown() {
        AppMainRunner.getJettyServerInstance().stop();
    }

    @Test
    void transfers_whenOneOrMoreTransfersAreDoneAtTheSameTime_shouldExecuteAllOfThemSuccessfully() throws IOException {
        TransferDTO[] transfersToDo = this.objectMapper.readValue(
                Paths.get("src/test/resources/test-files/multiples-transfers-input-01.json").toFile(),
                TransferDTO[].class);
        List<TransferExecutor> transferExecutorList = new LinkedList<>();
        stream(transfersToDo).forEach(transferDTO -> transferExecutorList.add(new TransferExecutor(transferDTO)));

        transferExecutorList.parallelStream().forEach(TransferExecutor::run);

        for(TransferExecutor executor: transferExecutorList) {
            assertEquals(200, executor.getResult().statusCode());
        }
    }
}
