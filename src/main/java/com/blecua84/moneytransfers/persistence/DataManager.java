package com.blecua84.moneytransfers.persistence;

import org.hibernate.SessionFactory;

public interface DataManager {

    SessionFactory getSessionFactory();
}
