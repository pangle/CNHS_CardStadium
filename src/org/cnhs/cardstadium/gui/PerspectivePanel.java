/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.gui;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JLayeredPane;

/**
 *
 * @author pangle
 */
public class PerspectivePanel extends JLayeredPane implements MouseListener, MouseMotionListener, ComponentListener, KeyListener {

    PerspectiveStadiumPanel stadiumLayer;
    PerspectiveCardAnimationPanel cardLayer;
    PerspectiveEditorPanel editorLayer;

    public PerspectivePanel() {
        stadiumLayer = new PerspectiveStadiumPanel();
        stadiumLayer.setOpaque(false);
        stadiumLayer.setBounds(0, 0, 200, 200);
        stadiumLayer.setVisible(true);
        //this.addMouseListener(editorLayer);
        
        editorLayer = new PerspectiveEditorPanel();
        editorLayer.setOpaque(false);
        editorLayer.setBounds(0, 0, 200, 200);
        editorLayer.setVisible(true);
        this.addMouseListener(editorLayer);
        this.addMouseMotionListener(editorLayer);
        
        cardLayer = new PerspectiveCardAnimationPanel(editorLayer);
        cardLayer.setOpaque(false);
        cardLayer.setBounds(0, 0, 200, 200);
        cardLayer.setVisible(true);
        this.addMouseListener(cardLayer);
        this.addMouseMotionListener(cardLayer);
        this.addKeyListener(cardLayer);

        

        this.add(stadiumLayer, JLayeredPane.PALETTE_LAYER);
        this.add(cardLayer, JLayeredPane.MODAL_LAYER);
        this.add(editorLayer, JLayeredPane.POPUP_LAYER);

        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        
        editorLayer.setBounds(0, 0, this.getWidth(), this.getHeight());
        editorLayer.revalidate();
        
        cardLayer.setBounds(0, 0, this.getWidth(), this.getHeight());
        cardLayer.revalidate();
        
        stadiumLayer.setBounds(0, 0, this.getWidth(), this.getHeight());
        stadiumLayer.revalidate();
        
        stadiumLayer.repaint();
        
        cardLayer.setScale(stadiumLayer.getScale());
        cardLayer.repaint();
        
        editorLayer.setScale(stadiumLayer.getScale());
        editorLayer.repaint();
        
        validate();
        setVisible(true);
    }

    public PerspectiveStadiumPanel getStadiumLayer() {
        return stadiumLayer;
    }

    public PerspectiveEditorPanel getEditorLayer() {
        return editorLayer;
    }

    public PerspectiveCardAnimationPanel getCardLayer() {
        return cardLayer;
    }
    
    

    @Override
    public void mouseClicked(MouseEvent e) {
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
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        editorLayer.setBounds(0, 0, this.getWidth(), this.getHeight());
        editorLayer.revalidate();
        
        cardLayer.setBounds(0, 0, this.getWidth(), this.getHeight());
        cardLayer.revalidate();
        
        stadiumLayer.setBounds(0, 0, this.getWidth(), this.getHeight());
        stadiumLayer.revalidate();
        
        stadiumLayer.repaint();
        
        cardLayer.setScale(stadiumLayer.getScale());
        cardLayer.repaint();
        
        editorLayer.setScale(stadiumLayer.getScale());
        editorLayer.repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        
    }

    @Override
    public void componentShown(ComponentEvent e) {
        
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
