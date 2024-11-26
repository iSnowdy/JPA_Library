package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY;
    // Logger?

    static {
        SessionFactory tempSessionFactory;
        try {
            // Predetermined configuration for SessionFactory
            tempSessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            System.out.println("SessionFactory initialized successfully");
        } catch (Throwable ex) {
            System.err.println("Failed to initialize SessionFactory: " + ex.getMessage());
            ex.printStackTrace();
            tempSessionFactory = null;
        }
        SESSION_FACTORY = tempSessionFactory;
    }
    // Consider removing it. Object is now static
    public static SessionFactory getSessionFactory() {
        if (SESSION_FACTORY == null) {
            throw new IllegalStateException("SessionFactory was not initialized successfully");
        }
        return SESSION_FACTORY;
    }

    public static Session openSession() {
        if (SESSION_FACTORY == null) {
            throw new IllegalStateException("SessionFactory is not initialized");
        }
        System.out.println("Opening a new Hibernate session");
        return SESSION_FACTORY.openSession();
    }
    // Probably don't need it due to try-with-resources usage
    public static void closeSession(Session session) {
        if (session != null) {
            try {
                session.close();
                System.out.println("Session closed successfully");
            } catch (Exception ex) {
                System.err.println("Failed to close session: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void closeSessionFactory() {
        if (SESSION_FACTORY != null) {
            try {
                SESSION_FACTORY.close();
                System.out.println("SessionFactory closed successfully");
            } catch (Exception ex) {
                System.err.println("Failed to close SessionFactory: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}