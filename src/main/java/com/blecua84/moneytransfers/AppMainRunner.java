package com.blecua84.moneytransfers;

import com.blecua84.moneytransfers.core.impl.DefaultContextContainer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
public class AppMainRunner {

    public static void main(String[] args) throws Exception {
        Instant startingTime = Instant.now();
        printHeader();

        DefaultContextContainer.init();
        DefaultContextContainer.start();

        printTimeElapsed(startingTime);
    }

    public static void stop() {
        DefaultContextContainer.stop();
    }

    private static void printTimeElapsed(Instant startTime) {
        Duration timeElapsed = Duration.between(startTime, Instant.now());
        log.debug("Time elapsed: " + timeElapsed.toMillis() / 1000f + " seconds");
    }

    private static void printHeader() {
        InputStream headerFile = ClassLoader.getSystemClassLoader().getResourceAsStream("header.txt");
        assert headerFile != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(headerFile));
        log.info(readAllLines(reader));
    }

    private static String readAllLines(BufferedReader reader) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
