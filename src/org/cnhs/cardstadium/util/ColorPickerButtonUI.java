/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author pangle
 */
public class ColorPickerButtonUI extends BasicButtonUI {
    private static Image unpressed;
    private static Image pressed;
    private static Image disabled;
    private static boolean imagesLoaded = false;
    
    public ColorPickerButtonUI() {
        if (!imagesLoaded) {
            unpressed = new ImageIcon(getClass().getResource("/org/cnhs/cardstadium/util/ColorPickerButton_Normal.png")).getImage();
            pressed = new ImageIcon(getClass().getResource("/org/cnhs/cardstadium/util/ColorPickerButton_Pressed.png")).getImage();
            disabled = new ImageIcon(getClass().getResource("/org/cnhs/cardstadium/util/ColorPickerButton_Disabled.png")).getImage();
            imagesLoaded = true;
        }
    }

    @Override
    public void paint(Graphics g, JComponent jc) {
        if(disabled == null || pressed == null || unpressed == null) {
            super.paint(g, jc);
        } else {
            ColorPickerButton b = (ColorPickerButton) jc;
            g.setColor(b.getColor());
            g.fillRect(3, 3, 24-6, 24-6);
            if (b.isEnabled()) {
                if (b.getModel().isPressed()) {
                    g.drawImage(pressed, 0, 0, jc);
                } else {
                    g.drawImage(unpressed, 0, 0, jc);
                }
            } else {
                g.drawImage(disabled, 0, 0, jc);
            }
        }
    }
}
