/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JComponent;

/**
 *
 * @author workstation
 */
public class ImageUtil {
    /**
     * Converts the given Image to a BufferedImage. Solves issues with the fact
     *   that Image objects are platform specific.
     * @param img Image
     * @return BufferedImage
     */
    public static BufferedImage getBufferedImage(Image img) {
        // The image must not be null
        assert img != null : "ImageUtil.getBufferedImage(Image img) - The provided Image must not be null.";
        
        // Create the new BufferedImage canvas
        BufferedImage bufimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        
        // Draw onto the BufferedImage
        bufimg.createGraphics().drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
        
        // Return the new BufferedImage
        return bufimg;
    }
    
    /**
     * Load an image with the given file location URL.
     * @param location
     * @param requestor
     * @return 
     */
    public static Image loadImageWithLocation(URL location, JComponent requestor) {
        Image image = null;

        try {
            image = java.awt.Toolkit.getDefaultToolkit().createImage(location);
            MediaTracker t = new MediaTracker(requestor);
            t.addImage(image, (int) Math.random() * 10000);
            waitForAllImages(t);
        } catch (Exception ex) {
            System.err.println("ImageUtil.loadImageWithLocation(String location, MediaTracker t, Object requestor) - Failed.");
        }


        return image;
    }
    
    /**
     * Load an image with the given file location String.
     * @param location
     * @param requestor
     * @return 
     */
    public static Image loadImageWithLocation(String location, JComponent requestor) {
        try {
            return loadImageWithLocation(new URL(location), requestor);
        } catch (MalformedURLException ex) {
            System.err.println("ImageUtil.loadImageWithLocation(String location, JComponent requestor) - Failed due to malformed URL: \"" + location + "\".");
        } finally {
            return null;
        }
    }
    
    /**
     * Load an image with the given class path such as, "org/cnhs/cardstadium"
     * @param classpathLocation
     * @param requestor
     * @return 
     */
    public static Image loadImageWithClasspathLocation(String classpathLocation, JComponent requestor) {
        return loadImageWithLocation(requestor.getClass().getClassLoader().getResource(classpathLocation), requestor);
    }
    
    /**
     * Create an image within the given dimensions, scaled to the top-left corner.
     * @param i
     * @param newImageWidth
     * @param newImageHeight
     * @param alpha
     * @return 
     */
    public static BufferedImage createImageInSize(BufferedImage i, int newImageWidth, int newImageHeight, float alpha) {
        BufferedImage newImage = new BufferedImage(newImageWidth, newImageHeight, BufferedImage.TYPE_INT_ARGB);

        float proposedXScale = (float)newImageWidth/(float)i.getWidth();
        float proposedYScale = (float)newImageHeight/(float)i.getHeight();

        float scale = (proposedXScale < proposedYScale) ? proposedXScale : proposedYScale;

        if (scale > 1.0f) {
            scale = 1.0000f;
        }

        int imageX = (int) (i.getWidth() * scale);
        int imageY = (int) (i.getHeight() * scale);

        BufferedImage scaledInstance = ImageUtil.getBufferedImage(i.getScaledInstance(
                imageX, imageY, Image.SCALE_SMOOTH));
        Graphics g = newImage.getGraphics();
        
        g.drawImage(scaledInstance, 0, 0, null);
        return newImage;
    }
    
    /**
     * Wait for all images in the given MediaTracker. This method blocks the thread.
     * @param t 
     */
    private static void waitForAllImages(MediaTracker t) {
        try {
            t.waitForAll();
        } catch (InterruptedException ex) {
            System.err.println("ImageUtil.waitForAllImages() - Failed.");
        }
    }
}
