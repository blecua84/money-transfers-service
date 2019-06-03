package com.blecua84.moneytransfers.services.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultTransfersServiceTest {

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(DefaultTransfersService.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        DefaultTransfersService firstInstance = DefaultTransfersService.getInstance();
        DefaultTransfersService secondInstance = DefaultTransfersService.getInstance();

        assertEquals(firstInstance, secondInstance);
    }
}
