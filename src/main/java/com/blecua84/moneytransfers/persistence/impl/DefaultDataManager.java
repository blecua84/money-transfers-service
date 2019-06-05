package com.blecua84.moneytransfers.persistence.impl;

import com.blecua84.moneytransfers.persistence.DataManager;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DefaultDataManager implements DataManager {

    private static DefaultDataManager instance;

    private SessionFactory sessionFactory;

    private DefaultDataManager() {
        this.sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public static DefaultDataManager getInstance() {
        if (instance == null) {
            instance = new DefaultDataManager();
        }

        return instance;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
