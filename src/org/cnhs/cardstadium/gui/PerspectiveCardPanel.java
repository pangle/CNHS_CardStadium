/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.cnhs.cardstadium.model.Sequence;
import org.cnhs.cardstadium.util.PointUtil;

/**
 * TODO: THIS IS A MAJOR PROBLEM SPOT IN THE CODE. VERY SLOW PERFORMANCE DUE TO
 * REDRAWS. THROWS HEAPSPACE EXCEPTIONS EVEN AT 1GB MEMORY. TEMPORARY FIX IS
 * NULLIFING PREVIOUS CARDS, COLLECTING GARBAGE, AND THEN GETTING NEW CARDS. GC
 * IS EXPENSIVE.
 * @author workstation
 */
public class PerspectiveCardPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
    private BufferedImage cards;
    private Sequence sequence;
    private int step = -1;
    private int error = -1;
    private PerspectiveEditorPanel editorPane;
    
    private int paintColor = -1;
    
    private int cachedStep;
    private Sequence cachedSequence;
    
    private StepArtist artist;

    private double scale = 0;
    
    public PerspectiveCardPanel(PerspectiveEditorPanel editorPane) {
        this.editorPane = editorPane;
        this.artist = new StepArtist(editorPane);
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
        this.artist.setSequence(sequence);
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
        this.artist.setStepNum(step);
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
        this.artist.setError(error);
    }

    public PerspectiveEditorPanel getEditorPane() {
        return editorPane;
    }

    public void setEditorPane(PerspectiveEditorPanel editorPane) {
        this.editorPane = editorPane;
        this.artist.setPerspectiveEditPanel(editorPane);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        this.artist.setScale(scale);
    }
    
    private boolean shouldProcess() {
        return sequence != null && step != -1 && error != -1;
    }
    
    private boolean shouldUpdate() {
        return sequence != cachedSequence || step != cachedStep || error > 0;
    }
    
    private void updated() {
        cachedSequence = sequence;
        cachedStep = step;
    }

    public void updateDisplay() {
        if (shouldProcess()) {
            this.cards = null;
            System.gc();
            this.cards = artist.drawFrame();
            updated();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //if (shouldUpdate()) {
            updateDisplay();
        //}
        if (cards != null && sequence != null && step != -1 && error != -1) {
            g.drawImage(cards, 0, 0, this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (shouldProcess()) {
            if (paintColor == -1) {
                if (sequence.changeCardSide(editorPane, PointUtil.descalePoint(e.getPoint(), scale), step)) {
                    updateDisplay();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (paintColor != -1) {
            if (e.getKeyCode() == KeyEvent.VK_ALT) {
                paintColor = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                paintColor = 2;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_2) {
            paintColor = -1;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (paintColor != -1) {
            if (sequence.changeCardSide(editorPane, PointUtil.descalePoint(e.getPoint(), scale), step, paintColor)) {
                updateDisplay();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
