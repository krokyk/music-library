/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.db;

import java.io.Serializable;
import java.util.TreeSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.kroky.musiclib.db.entities.AbstractEntity;
import org.kroky.musiclib.db.entities.Band;
import org.kroky.musiclib.db.entities.RecentDir;

/**
 *
 * @author Kroky
 */
public class DAO {

    private static final Logger LOG = LogManager.getLogger();

    private static final DbManager dbm = DbManager.getInstance();

    /**
     * FOR TESTING
     *
     * @param args
     * @throws Exception
     */
    //<editor-fold defaultstate="collapsed" desc="MAIN METHOD FOR TESTING PURPOSES">
    public static void main(String[] args) throws Exception {
        final Band band = new Band();
        band.setName("ACROSS THE RAIN");
        saveOrUpdate(band);
    }
    //</editor-fold>

    public static void rollback(Transaction tx) {
        if (tx == null) {
            return;
        }
        try {
            tx.rollback();
        } catch (Exception e) {
            LOG.error("Unable to rollback the transaction.", e);
        }
    }

//    public static Band getBand(String name) {
//        Band band = null;
//        final Session session = dbm.getSession();
//        Transaction tx = null;
//        try {
//            tx = session.beginTransaction();
//            band = (Band) session.get(Band.class, name);
//            tx.commit();
//        } catch (HibernateException e) {
//            LOG.error("Unable to commit.", e);
//            rollback(tx);
//        } finally {
//            closeQuietly(session);
//        }
//        return band;
//    }
    public static <T> T get(Class<T> c, Serializable id) {
        T entity = null;
        final Session session = dbm.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            entity = (T) session.get(c, id);
            tx.commit();
        } catch (HibernateException e) {
            LOG.error("Unable to commit.", e);
            rollback(tx);
        } finally {
            closeQuietly(session);
        }
        return entity;
    }

    private static void closeQuietly(final Session session) throws HibernateException {
        try {
            session.close();
        } catch (HibernateException hibernateException) {
            LOG.trace("Unable to close session", hibernateException);
        }
    }

    public static void refresh(AbstractEntity entity) {
        final Session session = dbm.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.refresh(entity);
            tx.commit();
        } catch (HibernateException e) {
            LOG.error("Unable to refresh " + entity.toDetailedString(), e);
            rollback(tx);
        } finally {
            closeQuietly(session);
        }
    }

    public static void saveOrUpdate(AbstractEntity entity) {
        final Session session = dbm.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(entity);
            tx.commit();
        } catch (HibernateException e) {
            LOG.error("Unable to save or update entity: " + entity.toDetailedString(), e);
            rollback(tx);
        } finally {
            closeQuietly(session);
        }
    }

    public static void delete(AbstractEntity entity) {
        final Session session = dbm.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        } catch (HibernateException e) {
            LOG.error("Unable to delete entity: " + entity.toDetailedString(), e);
            rollback(tx);
        } finally {
            closeQuietly(session);
        }
    }

    public static TreeSet<RecentDir> getRecentDirs() {
        TreeSet<RecentDir> dirs = new TreeSet<>();
        final Session session = dbm.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            dirs.addAll(session.createQuery("from RecentDir").list());
            tx.commit();
        } catch (HibernateException e) {
            LOG.error("Unable to commit.", e);
            rollback(tx);
        } finally {
            closeQuietly(session);
        }
        return dirs;
    }
}
