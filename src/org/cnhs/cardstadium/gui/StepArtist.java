/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import org.cnhs.cardstadium.model.Sequence;
import org.cnhs.cardstadium.util.PointUtil;

/**
 * A set of methods to draw a step from a Sequence.
 *
 * @author jcox
 * @version Aug 31, 2012
 */
public class StepArtist {

    private int framesPerStep = 10;
    private int frameNum = 0;
    private int nextStepNum = 0;
    private int oldStepNum = 0;
    private int[][] step;
    private ArrayList<Point> toBeFlipped = new ArrayList<Point>();
    private int numFlippedPerFrame;
    private int error = 0;
    private double scale = 1;
    private Sequence sequence = new Sequence();
    private PerspectiveEditorPanel perspectiveEditPanel;
    private boolean animating = false;

    /**
     * Creates an instance of Step artist using the default values.
     *
     * @param pep the PerspectiveEditorPanel to be used by StepArtist
     */
    public StepArtist(PerspectiveEditorPanel pep) {
        this.perspectiveEditPanel = pep;
    }

    /**
     * Creates an instance of Step artist using the default values.
     *
     * @param pep the PerspectiveEditorPanel to be used by StepArtist
     * @param seq the Sequence from which this StepArtist should draw
     */
    public StepArtist(PerspectiveEditorPanel pep, Sequence seq) {
        this.perspectiveEditPanel = pep;
        setSequence(seq);
        setStepNum(0);
    }

    /**
     * Creates an instance of Step artist using the default values.
     *
     * @param pep the PerspectiveEditorPanel to be used by StepArtist
     * @param seq the Sequence from which this StepArtist should draw
     * @param initialStep the first step this StepArtist should draw
     * @param framesPerStep the number of times one step should be drawn before
     * moving on to the next step
     * @param error the maximum error(in pixels) that each card could have
     * @param scale a number by which every point is multiplied to scale image
     */
    public StepArtist(PerspectiveEditorPanel pep, Sequence seq, int initialStep,
            int framesPerStep, int error, double scale) {
        this.perspectiveEditPanel = pep;
        setSequence(seq);
        this.nextStepNum = initialStep;
        setStepNum(initialStep);
        this.framesPerStep = framesPerStep;
        this.error = error;
        this.scale = scale;
    }

    /**
     * Draws a step from a sequence onto a Buffered Image.
     *
     * @param seq the Sequence the step is part of
     * @param stepNum the index of the step to be drawn
     * @param pep the PerspectiveEditorPanel containing the polygons that
     * represent the cards
     * @param error the maximum error(in pixels) that each card could have
     * @param scale a number by which every point is multiplied to scale image
     * @return a 1024x1024 ARGB BufferedImage
     */
    public static BufferedImage getStepImage(Sequence seq, int[][] step,
            PerspectiveEditorPanel pep, int error, double scale) {
        //store the card size and spacing
//        Dimension cardSize = seq.getStadium().getCardSize();
//        Dimension cardSpacing = seq.getStadium().getCardSpacing();
        //store the colors
        Color c1 = seq.getC1();
        Color c2 = seq.getC2();
        //get the polygons to draw
        Polygon[][] polys = pep.getPolygonsForPerspectiveGrid();
        //determine the size of the BufferedImage to be drawn
        int x1 = polys[polys.length - 1][0].xpoints[1]; //upper right point of upper right polygon
        int x2 = polys[polys.length - 1][polys[0].length - 1].xpoints[2]; //lower right point of lower right polygon
        int y1 = polys[0][polys[0].length - 1].ypoints[3]; //lower left point of lower left polygon
        int y2 = polys[polys.length - 1][polys[0].length - 1].ypoints[2]; //lower right point of lower right polygon
        //create a ARGB BufferedImage
        BufferedImage img = new BufferedImage(Math.max(x1, x2), Math.max(y1, y2),
                BufferedImage.TYPE_INT_ARGB);
        //get the Graphics object so it can draw on the BufferedImage
        Graphics g = img.getGraphics();
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //create a Random object
        Random randy = new Random();
        //draw the step
        for (int c = 0; c < polys.length && c < step.length; c++) {
            for (int r = 0; r < polys[c].length && r < step[c].length; r++) {
                //set the color as c1
                g.setColor(c1);
                //if it should be c2, change the color
                if (step[c][r] == 1) {
                    g.setColor(c2);
                }
                //apply error
                int[] xVals = polys[c][r].xpoints; //get x points
                int[] yVals = polys[c][r].ypoints; //get y points
                Point scaledAndAdjustedPoint;
                Polygon card = new Polygon(); // the polygon with error applied
                for (int i = 0; i < xVals.length && i < yVals.length; i++) {
                    //shift the x and y vals by a random number between +- error
                    if (error > 0) {
                        xVals[i] += randy.nextInt(error * 2) - error;
                        yVals[i] += randy.nextInt(error * 2) - error;
                    }
                    scaledAndAdjustedPoint = new Point(xVals[i], yVals[i]);
                    scaledAndAdjustedPoint = PointUtil.scalePoint(scaledAndAdjustedPoint, scale);
                    card.addPoint(scaledAndAdjustedPoint.x, scaledAndAdjustedPoint.y); //add the new point

                }
                //draw the card in the polygon
                g.fillPolygon(card.xpoints, card.ypoints, card.npoints);
            }
        }
        return img;
    }

    public static BufferedImage getStepImage(Sequence seq, int stepNum,
            PerspectiveEditorPanel pep, int error, double scale) {
        return StepArtist.getStepImage(seq, seq.getStep(stepNum), pep, error,
                scale);
    }

    /**
     * Gets the number of times one step should be drawn before
     *
     * @return frames per step
     */
    public int getFramesPerStep() {
        return framesPerStep;
    }

    /**
     * Set the number of times one step should be drawn before
     *
     * @param framesPerStep frames per step
     */
    public void setFramesPerStep(int framesPerStep) {
        this.framesPerStep = framesPerStep;
    }

    /**
     * Get index of the step that will be drawn the next time draw() is called
     *
     * @return current step num
     */
    public int getStepNum() {
        return nextStepNum;
    }

    /**
     * Set index of the step that will be drawn the next time draw() is called
     *
     * @param stepNum current step num
     */
    public final void setStepNum(int stepNum) {
        oldStepNum = nextStepNum;
        nextStepNum = stepNum;
        toBeFlipped = getFlippedCards(oldStepNum, nextStepNum);
        numFlippedPerFrame = (int) Math.max((float)toBeFlipped.size()
                / (float)framesPerStep, 1);
        copyStep(oldStepNum);
//        step = sequence.getStep(oldStepNum);
//        System.arraycopy(sequence.getStep(oldStepNum), 0, step, 0, step.length);
    }

    /**
     * Get the maximum error(in pixels) that each card could have
     *
     * @return error (in pixels)
     */
    public int getError() {
        return error;
    }

    /**
     * Set the maximum error(in pixels) that each card could have
     *
     * @param error error (in pixels)
     */
    public void setError(int error) {
        this.error = error;
    }

    /**
     * Get the number by which every point is multiplied to scale image
     *
     * @return scale factor
     */
    public double getScale() {
        return scale;
    }

    /**
     * Set a number by which every point is multiplied to scale image
     *
     * @param scale scale factor
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Get the Sequence from which this StepArtist should draw
     *
     * @return sequence
     */
    public Sequence getSequence() {
        return sequence;
    }

    /**
     * Set the Sequence from which this StepArtist should draw
     *
     * @param sequence sequence
     */
    public final void setSequence(Sequence sequence) {
        this.sequence = sequence;
        step = new int[sequence.getGridSize().width][sequence.getGridSize().height];
    }

    /**
     * Get the PerspectiveEditorPanel to be used by StepArtist
     *
     * @return perspective editor panel
     */
    public PerspectiveEditorPanel getPerspectiveEditPanel() {
        return perspectiveEditPanel;
    }

    /**
     * Set the PerspectiveEditorPanel to be used by StepArtist
     *
     * @param pep perspective editor panel
     */
    public void setPerspectiveEditPanel(PerspectiveEditorPanel pep) {
        this.perspectiveEditPanel = pep;
    }

    /**
     * Draws a frame
     * @return a buffered image of the cards
     */
    public BufferedImage drawFrame() {
        frameNum++;
        Random randy = new Random();
        Point p;
        int randomIndex;
        //if nothing changes, return without making any changes
        for(int i = 0; i < numFlippedPerFrame && !toBeFlipped.isEmpty(); i++){
            randomIndex = randy.nextInt(toBeFlipped.size());
            p = toBeFlipped.get(randomIndex);
            step[p.x][p.y] = Math.abs(step[p.x][p.y] - 1);
            toBeFlipped.remove(randomIndex);
        }
        //determine whether or  not the stepartist still needs to animate more
        animating = !toBeFlipped.isEmpty();
        //draw using step
        return StepArtist.getStepImage(sequence, step, perspectiveEditPanel,
                error, scale);
    }

    /**
     * Whether or not calling drawFrame() would return a different image than it
     * did the last time it was called
     * @return
     */
    public boolean isAnimating(){
        return animating;
    }

    /**
     * Gets the coordinates of the cards that change between oldStep and newStep
     * @param oldStep the index of the step you start with
     * @param newStep the index of the step you are going to
     * @return an ArrayList of Points representing card locations
     */
    private ArrayList<Point> getFlippedCards(int oldStepNum, int newStepNum){
        int[][] oldStep = sequence.getStep(oldStepNum);
        int[][] newStep = sequence.getStep(newStepNum);
        ArrayList<Point> flipped = new ArrayList<Point>();
        for (int c = 0; c < sequence.getGridSize().width; c++){
            for (int r = 0; r < sequence.getGridSize().height; r++){
                if(oldStep[c][r] != newStep[c][r]){
                    flipped.add(new Point(c, r));
                }
            }
        }
        return flipped;
    }

    /**
     * Copies all of the values in sequence.getStep(stepNum) to step
     *
     * @param stepNum the step from which to copy
     */
    private void copyStep(int stepNum){
        for(int c = 0; c < step.length && c < sequence.getStep(stepNum).length;
                c++){
            for(int r = 0; r < step[c].length
                    && r < sequence.getStep(stepNum)[c].length; r++){
                step[c][r] = sequence.getStep(stepNum)[c][r];
            }
        }
    }
}
