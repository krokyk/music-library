/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 *
 * @author user
 */
public class DbManager {

    private static final Logger LOG = LogManager.getLogger();

    private SessionFactory sessionFactory;
    private final HashSet<Session> sessions = new HashSet<>();
    private static final DbManager INSTANCE = new DbManager();
    private boolean initialized = false;

    private DbManager() {

    }

    public static DbManager getInstance() {
        return INSTANCE;
    }

    public Session getSession() {
        init();
        Session session = sessionFactory.openSession();
        // if (!session.isOpen()) {
        // session = sessionFactory.openSession();
        // }
        sessions.add(session);
        return session;
    }

    public void createDb() {
        Connection connection = null;
        try {
            final File dbDir = new File("musiclibdb");
            if (!dbDir.exists()) {
                LOG.debug("Creating database...");
                connection = DriverManager
                        .getConnection("jdbc:derby:musiclibdb;user=musiclibdb;password=musiclibdb;create=true");
            }
        } catch (SQLException ex) {
            LOG.error("Cannot create database.", ex);
        } finally {
            if (connection != null) {
                try {
                    LOG.debug("Closing connection that was used to create the database...");
                    connection.close();
                } catch (SQLException ex) {
                    LOG.warn("Cannot close connection after database creation.", ex);
                }
            }
        }
    }

    public void shutdownDb() {
        String msg = "Disconnecting and closing Hibernate sessions...";
        LOG.info(msg);
        sessions.stream().map((session) -> {
            try {
                if (session.isOpen()) {
                    session.disconnect();
                }
            } catch (Exception e) {
                LOG.warn("Unable to disconnect session", e);
            }
            return session;
        }).forEach((session) -> {
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (HibernateException hibernateException) {
                LOG.warn("Unable to close session", hibernateException);
            }
        });
        msg = "Closing Hibernate session factory...";
        LOG.info(msg);
        try {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        } catch (HibernateException hibernateException) {
            LOG.warn("Unable to close session factory", hibernateException);
        }
        msg = "Shutting down DB...";
        LOG.info(msg);
        try {
            // the shutdown=true attribute shuts down Derby
            DriverManager.getConnection("jdbc:derby:;shutdown=true");

            // To shut down a specific database only, but keep the
            // engine running (for example for connecting to other
            // databases), specify a database in the connection URL:
            // DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
        } catch (SQLException ex) {
            if (((ex.getErrorCode() == 50000) && ("XJ015".equals(ex.getSQLState())))) {
                // we got the expected exception
                msg = "Derby shut down normally";
                LOG.info(msg);
                // Note that for single database shutdown, the expected
                // SQL state is "08006", and the error code is 45000.
            } else {
                // if the error code or SQLState is different, we have
                // an unexpected exception (shutdown failed)
                msg = "Derby did not shut down normally";
                LOG.error(msg, ex);
            }
        }
    }

    private void init() {
        if (!initialized) {
            // try {
            // Configuration configuration = new Configuration().configure();
            // ServiceRegistry serviceRegistry = new
            // StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            // sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure().build();
            Metadata metadata = new MetadataSources(standardRegistry).getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
            initialized = true;
            // } catch (HibernateException hibernateException) {
            // Throwable cause = hibernateException.getCause();
            // while (true) {
            // if (cause.getCause() == null) {
            // break;
            // }
            // cause = cause.getCause();
            // }
            // if (cause instanceof StandardException) {
            // StandardException derbyEx = (StandardException) cause;
            // if ("XSDB6".equals(derbyEx.getSQLState())) { //db instance is already running
            // LOG.error("The DB is already started by a different JVM. Please disconnect offending JVM and try
            // again.");
            // } else {
            // LOG.error("Cannot initialize DbManager", hibernateException);
            // }
            // } else {
            // LOG.error("Cannot initialize DbManager", hibernateException);
            // }
            // }
        }
    }
}
