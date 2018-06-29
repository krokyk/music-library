/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.jobs;

import foxtrot.Job;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import org.kroky.musiclib.gui.MainFrame;

/**
 *
 * @author user
 */
abstract class AbstractJob extends Job {

    protected final MainFrame mainFrame;
    protected final JProgressBar pb;

    public AbstractJob(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.pb = mainFrame.getProgressBar();
    }

    protected void resetProgressBar() {
        pb.setIndeterminate(false);
        pb.setStringPainted(false);
        pb.setString(null);
        pb.setValue(0);
    }

    protected void initProgressBar(int maxValue) {
        initProgressBar(null, false, maxValue);
    }

    protected void initProgressBar(String pbString, boolean indeterminate, int maxValue) {
        pb.setMinimum(0);
        pb.setValue(0);
        pb.setMaximum(maxValue);
        pb.setIndeterminate(indeterminate);
        if (pbString != null) {
            pb.setStringPainted(true);
            pb.setString(pbString);
        }
    }

    protected void setProgressText(String pbString) {
        if (pbString != null) {
            pb.setStringPainted(true);
            pb.setString(pbString);
        }
    }

    protected void incProgressBar() {
        pb.setValue(pb.getValue() + 1);
    }

    protected void toggle(Component... components) {
        for (Component c : components) {
            c.setEnabled(!c.isEnabled());
        }
    }

    protected void disableControls() {
        setControlsEnabled(mainFrame.getContentPane(), false);
    }

    protected void enableControls() {
        setControlsEnabled(mainFrame.getContentPane(), true);
    }

    private void setControlsEnabled(Container container, boolean enabled) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JButton || component instanceof JRadioButton || component instanceof JComboBox) {
                component.setEnabled(enabled);
            } else if (component instanceof Container) {
                setControlsEnabled((Container) component, enabled);
            }
        }
    }
}
