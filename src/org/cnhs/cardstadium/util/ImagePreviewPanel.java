/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * http://www.javalobby.org/forums/thread.jspa?messageID=91844740
 */
package org.cnhs.cardstadium.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagePreviewPanel extends JPanel implements PropertyChangeListener {

    private JLabel label;
    private int maxImgWidth;

    public ImagePreviewPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JLabel("Preview:"), BorderLayout.NORTH);
        label = new JLabel();
        label.setBackground(Color.WHITE);
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(200, 200));
        maxImgWidth = 195;
        label.setBorder(BorderFactory.createEtchedBorder());
        add(label, BorderLayout.CENTER);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Icon icon = null;
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt
                .getPropertyName())) {
            File newFile = (File) evt.getNewValue();
            if (newFile != null) {
                String path = newFile.getAbsolutePath();
                if (path.endsWith(".gif") || path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".bmp")) {
                    try {
                        BufferedImage img = ImageIO.read(newFile);
                        float width = img.getWidth();
                        float height = img.getHeight();
                        float scale = height / width;
                        width = maxImgWidth;
                        height = (width * scale); // height should be scaled from new width							
                        icon = new ImageIcon(img.getScaledInstance(Math.max(1, (int) width), Math.max(1, (int) height), Image.SCALE_SMOOTH));
                    } catch (IOException e) {
                        // couldn't read image.
                    }
                }
            }

            label.setIcon(icon);
            this.repaint();

        }
    }
}
