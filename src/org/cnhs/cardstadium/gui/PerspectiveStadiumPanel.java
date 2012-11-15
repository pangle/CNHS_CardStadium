/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JPanel;
import org.cnhs.cardstadium.util.GameImageUtil;

/**
 *
 * @author pangle
 */
public class PerspectiveStadiumPanel extends JPanel {
    private BufferedImage stadium;
    
    
    
    public BufferedImage getStadium() {
        return stadium;
    }

    public void setStadium(BufferedImage stadium) {
        this.stadium = stadium;
    }
    
    public void setStadium(File file) {
        if (file.getName().equals("DEFAULT")) {
            this.stadium = GameImageUtil.getBufferedImage(GameImageUtil.loadImageWithClasspathLocation("org/cnhs/cardstadium/gui/DefaultStadium.png", new MediaTracker(this), this));
        } else {
            Image tmp = GameImageUtil.loadImageWithLocation(file.getAbsolutePath(), new MediaTracker(this), this);
            
            this.stadium = GameImageUtil.getBufferedImage(tmp);
        }
    }
    
    private BufferedImage cachedStadium;
    private Dimension cachedDimension;
     
    public PerspectiveStadiumPanel() {
        this.stadium = GameImageUtil.getBufferedImage(GameImageUtil.loadImageWithClasspathLocation("org/cnhs/cardstadium/gui/DefaultStadium.png", new MediaTracker(this), this));
    }
     
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (stadium != null) {
            if (cachedDimension == null || !cachedDimension.equals(this.getSize())) {
                cachedStadium = GameImageUtil.createImageInSize(stadium, this.getWidth(), this.getHeight(), 1.0f);
                cachedDimension = this.getSize();
            }
            g.drawImage(cachedStadium, 0, 0, this); 
        }
    }
    

    @Override
    public void setBounds(int i, int i1, int i2, int i3) {
        super.setBounds(i, i1, i2, i3);
        repaint();
    }
    
    public double getScale() {
        double computedWScale = ((double) this.getSize().getWidth()) / ((double) stadium.getWidth());
        double computedHScale = ((double) this.getSize().getHeight()) / ((double) stadium.getHeight());
        double computedScale = (computedWScale < computedHScale) ? computedWScale : computedHScale;
        return computedScale < 1.0 ? computedScale : 1;
    }
}
