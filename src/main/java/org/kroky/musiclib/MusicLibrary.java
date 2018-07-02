/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib;

import java.beans.Beans;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kroky.musiclib.gui.MainFrame;

/**
 *
 * @author user
 */
public class MusicLibrary {

    static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    static {
        if (System.getProperty("logFileName") == null || System.getProperty("logFileName").isEmpty()) {
            System.setProperty("logFileName", String.format("musiclib-%s.log", DATE_TIME_FORMAT.format(new Date())));
        }
    }

    private static final Logger LOG = LogManager.getLogger();

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        Beans.setDesignTime(true);
        if (System.getProperty("derby.system.home") == null || System.getProperty("derby.system.home").isEmpty()) {
            System.setProperty("derby.system.home", ".");
        }

        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            LOG.warn("Unable to initialize requested look'n'feel.", ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

}
