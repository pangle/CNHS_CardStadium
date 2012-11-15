/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.cnhs.cardstadium.model.Sequence;
import org.cnhs.cardstadium.util.PointUtil;

/**
 *
 * @author workstation
 */
public class PerspectiveCardAnimationPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    public static final int ANIMATION_FRAME_RATE = 30;
    
    private BufferedImage cards;
    private StepArtist artist;
    private double scale;
    
    private boolean forceUpdateOnTimer = false;
    
    private Timer animationTimer;
    
    private int paintColor = -1;
    
    private boolean updated = false;
    
    private Point lastPointScaled = null;
    
    /**
     * Create a new PerspectiveCardAnimationPanel (PCAP) with the given perspective editor.
     * @param editorPanel
     */
    public PerspectiveCardAnimationPanel(PerspectiveEditorPanel editorPanel) {
        this.setFocusable(true);
        this.addKeyListener(this);
        
        artist = new StepArtist(editorPanel);
        
        animationTimer = new Timer(1000/ANIMATION_FRAME_RATE, null);
        animationTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //System.out.println("Timer fired");
                if (artist.isAnimating() || updated || artist.getError() > 0) {
                //    System.out.println("Was animating");
                    forceUpdateOnTimer = false;
                    repaint();
                    updated = false;
                }
            }
        });
        animationTimer.start();
    }
    
    /**
     * Set the FPS (Frames per STEP) of the animation
     * @param fps 
     */
    public void setAnimationTiming(int fps) {
        artist.setFramesPerStep(fps);
        updated = true;
    }
    
    /**
     * Set the sequence of the panel
     * @param sequence
     */
    public void setSequence(Sequence sequence) {
        artist.setSequence(sequence);
        updated = true;
    }
    
    /**
     * Set the step of the panel
     * @param step
     */
    public void setStep(int step) {
        artist.setStepNum(step);
        updated = true;
    }
    
    /**
     * Get the current step
     * @return int
     */
    public int getStep() {
        return artist.getStepNum();
    }
    
    /**
     * Set the scale of the panel
     * @param scale
     */
    public void setScale(double scale) {
        artist.setScale(scale);
        this.scale = scale;
        updated = true;
    }
    
    /**
     * Set the error of the panel
     * @param error
     */
    public void setError(int error) {
        artist.setError(error);
        updated = true;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }
    
    public void setStepWithNoAnimation(int step) {
        setStepWithAnimationSpeed(step, 0);
    }
    
    public void setStepWithAnimationSpeed(int step, int fps) {
        int currentFramesPerStep = artist.getFramesPerStep();
            
        artist.setFramesPerStep(fps);
        artist.setStepNum(step);
            
        repaint();
            
        artist.setFramesPerStep(currentFramesPerStep);        
    }
    
    /**
     * Check to see if this panel is ready to display
     * @return
     */
    private boolean isReady() {
        return (artist.getSequence() != null && (new Integer(artist.getStepNum()) != null && artist.getStepNum() != -1) && (new Integer(artist.getError()) != null && artist.getError() != -1));
    }
    
    /**
     * Update the buffer for the display. Call only from paint(Graphics g).
     */
    private void updateDisplay() {
        if (isReady()) {
            this.cards = null;
            System.gc();
            this.cards = artist.drawFrame();
        }
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
                
        updateDisplay();
        
        // Draw the cards from the buffer onto the screen
        if (cards != null && isReady()) {
            g.drawImage(cards, 0, 0, this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isReady() && paintColor == -1) {
            if (artist.getSequence().changeCardSide(artist.getPerspectiveEditPanel(), PointUtil.descalePoint(e.getPoint(), scale), artist.getStepNum())) {
                setStep(getStep());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (paintColor != -1) {
            if (artist.getSequence().changeCardSide(artist.getPerspectiveEditPanel(), PointUtil.descalePoint(e.getPoint(), scale), artist.getStepNum(), paintColor)) {
                setStepWithNoAnimation(getStep());
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
    
}
