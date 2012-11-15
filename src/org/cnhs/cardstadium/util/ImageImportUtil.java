/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author pangle
 */
public class ImageImportUtil {

//    public static BufferedImage convertToBufferedImage(Image img, Dimension dim) {
//        BufferedImage bufferImage = new BufferedImage((int) dim.getWidth(), (int) dim.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        bufferImage.createGraphics().drawImage(img, 0, 0, null);
//        return bufferImage;
//    }

    public static BufferedImage getBufferedImage(Image img) {
        if (img == null) {
            return null;
        }
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        // draw original image to thumbnail image object and
        // scale it to the new size on-the-fly
        BufferedImage bufimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufimg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(img, 0, 0, w, h, null);
        g2.dispose();
        return bufimg;
    }

    /**
     * Create a new instance of the given image, and scale it down to fit within
     * the UXGA pixels area. UXGA is an area of 1600, 1200 pixels, meaning the
     * returned image can be up to 1.9 megapixels
     * @param source The BufferedImage you wish to scale.
     * @return BufferedImage scaled down to proper size.
     */
//    public static BufferedImage scaleToUXGA(BufferedImage source) {
//        if (source.getWidth() < 1600 || source.getHeight() < 1200) {
//            return source;
//        } else {
//            int scale = 1;
//            if (source.getWidth() > source.getHeight()) {
//                scale = source.getWidth() / 1600;
//            } else {
//                scale = source.getHeight() / 1200;
//            }
//            return getBufferedImage(source.getScaledInstance(source.getWidth() / scale, source.getHeight() / scale, BufferedImage.SCALE_SMOOTH));
//        }
//    }

    public static Image loadImageWithClasspathLocation(String location, MediaTracker t, Object requestor) {
        Image image = null;

        try {
            image = java.awt.Toolkit.getDefaultToolkit().createImage(requestor.getClass().getClassLoader().getResource(location));
            t.addImage(image, (int) Math.random() * 10000);
            waitForAllImages(t);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return image;
    }

    public static Image loadImageWithLocation(String location, MediaTracker t, Object requestor) {
        Image image = null;

        try {
            image = java.awt.Toolkit.getDefaultToolkit().createImage(location);
            t.addImage(image, (int) Math.random() * 10000);
            waitForAllImages(t);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return image;
    }

    private static void waitForAllImages(MediaTracker t) {
        try {
            t.waitForAll();
        } catch (InterruptedException e) {
            System.err.println("Image loading failed");
        }
    }

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

        BufferedImage scaledInstance = ImageImportUtil.getBufferedImage(i.getScaledInstance(
                imageX, imageY, Image.SCALE_SMOOTH));
        Graphics g = newImage.getGraphics();
        //g.setColor(new Color(0.0f, 0.0f, 0.0f, alpha));
        //g.fillRect(0, 0, newImageWidth, newImageHeight);
        //g.drawImage(scaledInstance, (newImageWidth-scaledInstance.getWidth())/2, (newImageHeight-scaledInstance.getHeight())/2, null);
        g.drawImage(scaledInstance, 0, 0, null);
        return newImage;
    }

    /**
     * Creates a step that will make a picture resembling the given image
     *
     * @param img the image to resemble
     * @param gridSize the dimensions of the step to be created
     * @return
     */
    public static int[][] getStepFromImage(BufferedImage img, Dimension gridSize, int bits){
        //convert image to grayscale
//        BufferedImageOp grayscaleConv = new ColorConvertOp(
//                ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//        BufferedImage grayImg = img;
//        grayscaleConv.filter(img, grayImg

        //scale image so each of the cards represents only one pixel
//        BufferedImage scaledImg = getBufferedImage(img.getScaledInstance(
//                gridSize.width, gridSize.height, Image.SCALE_AREA_AVERAGING));

        //convert image
        if (bits == 1){
            img = ConvertUtil.convert1(img);
        } else if (bits == 4){
            img = ConvertUtil.convert4(img);
        } else if (bits == 8){
            img = ConvertUtil.convert8(img);
        } else if (bits == 24){
            img = ConvertUtil.convert24(img);
        } else if (bits == 32){
            img = ConvertUtil.convert32(img);
        }

        //scale image so each of the cards represents only one pixel
        BufferedImage scaledImg = getBufferedImage(img.getScaledInstance(
                gridSize.width, gridSize.height, Image.SCALE_AREA_AVERAGING));

        //count colors
        HashMap<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
        int key;
        for(int x = 0; x < scaledImg.getWidth(); x++){
            for(int y = 0; y < scaledImg.getHeight(); y++){
                key = scaledImg.getRGB(x, y);
                if(!colorMap.containsKey(key)){
                    colorMap.put(key, 0);
                }
                colorMap.put(key, colorMap.get(key) + 1);
            }
        }
        //find which color is the most common
        Integer commonKey = getCommonKey(colorMap);
        Color commonColor1;
        if(commonKey == null){
            commonColor1 = Color.BLACK;
        } else {
            commonColor1 = new Color(commonKey);
            colorMap.remove(commonKey);
        }
        //find which color is the second most common
        commonKey = getCommonKey(colorMap);
        Color commonColor2;
        if(commonKey == null){
            commonColor2 = commonColor1;
        } else {
            commonColor2 = new Color(commonKey);
        }


        /*look at color values of all pixels and choose card color based on
         * which common color it is closest to
         */
        int[][] step = new int[gridSize.width][gridSize.height];
        int pixel;
        int rDif1, gDif1, bDif1, rDif2, gDif2, bDif2, totalDif1, totalDif2;
        for(int x = 0; x < gridSize.width; x++){
            for(int y = 0; y < gridSize.height; y++){
                pixel = scaledImg.getRGB(x, y);
                rDif1 = Math.abs(scaledImg.getColorModel().getRed(pixel)
                        - commonColor1.getRed());
                gDif1 = Math.abs(scaledImg.getColorModel().getGreen(pixel)
                        - commonColor1.getGreen());
                bDif1 = Math.abs(scaledImg.getColorModel().getBlue(pixel)
                        - commonColor1.getBlue());
                totalDif1 = rDif1 + gDif1 + bDif1;
                rDif2 = Math.abs(scaledImg.getColorModel().getRed(pixel)
                        - commonColor2.getRed());
                gDif2 = Math.abs(scaledImg.getColorModel().getGreen(pixel)
                        - commonColor2.getGreen());
                bDif2 = Math.abs(scaledImg.getColorModel().getBlue(pixel)
                        - commonColor2.getBlue());
                totalDif2 = rDif2 + gDif2 + bDif2;

                if(totalDif1 > totalDif2){
                    step[x][y] = 1;
                } else {
                    step[x][y] = 0;
                }
            }
        }

        return step;
    }

    private static Integer getCommonKey(HashMap<Integer,Integer> map){
         //find which key is the most common
        if(map.keySet().isEmpty()){
            return null;
        }
        Integer commonKey = (Integer) (map.keySet().toArray()[0]);
        for(int i = 1; i < map.size(); i++){
            if(map.get((Integer) map.keySet().toArray()[i])
                    > map.get(commonKey)){
                commonKey = (Integer) (map.keySet().toArray()[i]);
            }
        }
        return commonKey;
    }
}
