package com.blecua84.moneytransfers.persistence.impl;

import com.blecua84.moneytransfers.persistence.exceptions.DataManagerException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultDataManagerTest {

    @Test
    void getInstance_shouldCreateANewObject() {
        assertNotNull(DefaultDataManager.getInstance());
    }

    @Test
    void getInstance_whenInstanceIsAlreadyCreated_shouldReturnTheSameThanWasReturnedPreviously() {
        DefaultDataManager firstInstance = DefaultDataManager.getInstance();
        DefaultDataManager secondInstance = DefaultDataManager.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void getSessionFactory_shouldReturnSessionFactoryCreatedInConstructor() throws DataManagerException {
        DefaultDataManager dataManager = DefaultDataManager.getInstance();

        assertNotNull(dataManager.getSessionFactory());
    }
}
