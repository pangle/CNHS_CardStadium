/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;

/**
 *
 * @author pangle
 */
public class ColorPickerButton extends JButton {
    protected Color color;
    protected ColorPickerButtonDelegate delegate;
    public ColorPickerButton() {
        setSize(24, 24);
        setColor(Color.RED);
        setUI(new ColorPickerButtonUI());
        setBorderPainted(false);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pickColor();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(24, 24);
    }
    
    public void setColor(Color c) {
        color = c;
        repaint();
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setDelegate(ColorPickerButtonDelegate d) {
        delegate = d;
    }
    
    public ColorPickerButtonDelegate getDelegate() {
        return delegate;
    }
    
    private void pickColor() {
        Color newColor = JColorChooser.showDialog(this, "Colors", color);
        if (newColor != null) {
            color = newColor;
            if (delegate != null) {
                delegate.respondToColorChange(this, color);
            }
            repaint();
        }
    }
}
