/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author pangle
 */
public class ImageUtil {
    public static final String VALID_IMAGE_DESCRIPTION = "Image files (.PNG, .GIF, .JPG, .JPEG)";
    public static final String[] VALID_IMAGE_EXTENSIONS = {"PNG","GIF","JPG","JPEG"};
    
    public static void loadImageWithFileOpenDialog(Component parent, ImageDialogAction actions) {
        final JFileChooser fc = new JFileChooser();

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);

        fc.setFileFilter(new FileNameExtensionFilter(VALID_IMAGE_DESCRIPTION, VALID_IMAGE_EXTENSIONS));

        int choice = fc.showOpenDialog(parent);

        if (choice == JFileChooser.APPROVE_OPTION) {
            actions.imageWasLoaded(getBufferedImage(loadImageWithLocation(fc.getSelectedFile().toString(), new MediaTracker(parent), parent)), fc.getSelectedFile());
        } else {
            actions.imageWasNotLoaded();
        }
    }
    
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
     * Load an image from the given classpath location, in form "org/cnhs/cardstadium/myimg.png"
     * @param location
     * @param t
     * @param requestor
     * @return Image
     */
    public static Image loadImageWithClasspathLocation(String location, MediaTracker t, Object requestor) {
        Image image = null;

        try {
            image = java.awt.Toolkit.getDefaultToolkit().createImage(requestor.getClass().getClassLoader().getResource(location));
            t.addImage(image, (int) Math.random() * 10000);
            waitForAllImages(t);
        } catch (Exception e) {
            
        }


        return image;
    }

    /**
     * Load an image from the given location on disk.
     * @param location
     * @param t
     * @param requestor
     * @return Image
     */
    public static Image loadImageWithLocation(String location, MediaTracker t, Object requestor) {
        Image image = null;

        try {
            image = java.awt.Toolkit.getDefaultToolkit().createImage(location);
            t.addImage(image, (int) Math.random() * 10000);
            waitForAllImages(t);
        } catch (Exception e) {
            
        }

        return image;
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
