/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import org.cnhs.cardstadium.model.Sequence;

/**
 * Prints labels for a sequence
 *
 * @author jcox
 * @version Sep 18, 2012
 */
public class SequencePrinter implements Printable {

    private final Sequence MY_SEQUENCE;
    private final int H_LABEL_GAP;
    private final int V_LABEL_GAP;
    private final int LABEL_WIDTH;
    private final int LABEL_HEIGHT;
    private final int LABELS_PER_LINE;
    private final int LINES_PER_PAGE;
    private final int LABELS_PER_PAGE;
    private final String C1_NAME, C2_NAME;
    private final int VAL_GAP = 5;
    private final int NUM_PAGES;
    public final static int PIXELS_PER_INCH = 72;

    /**
     * Create a printable object
     *
     * @param seq the Sequence you will be printing
     * @param c1Name the human-readable name of the color on the first side of
     * the card
     * @param c2Name the human-readable name of the color on the second side of
     * the card
     * @param hLabelGap the horizontal gap, in inches, between the labels
     * @param vLabelGap the vertical gap, in inches, between the labels
     * @param labelWidth the width, in inches, of one label
     * @param labelHeight the height, in inches of one label
     * @param labelsPerLine the number of labels in one row
     * @param linesPerPage the number of lines on one page
     */
    public SequencePrinter(Sequence seq, String c1Name, String c2Name,
            double hLabelGap, double vLabelGap, double labelWidth,
            double labelHeight, int labelsPerLine, int linesPerPage) {
        super();
        MY_SEQUENCE = seq;
        C1_NAME = c1Name;
        C2_NAME = c2Name;
        H_LABEL_GAP = SequencePrinter.inchesToPixels(hLabelGap);
        V_LABEL_GAP = SequencePrinter.inchesToPixels(vLabelGap);
        LABEL_WIDTH = SequencePrinter.inchesToPixels(labelWidth);
        LABEL_HEIGHT = SequencePrinter.inchesToPixels(labelHeight);
        LABELS_PER_LINE = labelsPerLine;
        LINES_PER_PAGE = linesPerPage;
        LABELS_PER_PAGE = LABELS_PER_LINE * LINES_PER_PAGE;
        NUM_PAGES = (int) Math.ceil((double) (MY_SEQUENCE.getGridSize().width
                * MY_SEQUENCE.getGridSize().height) / LABELS_PER_PAGE);
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        // exit if there are no more pages which need to be printed
        if (pageIndex > NUM_PAGES - 1) {
            return NO_SUCH_PAGE;
        }

        // User (0,0) is typically outside the
        // imageable area, so we must translate
        // by the X and Y values in the PageFormat
        // to avoid clipping.
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // Now we perform our rendering
        System.out.println("LPL: " + LABELS_PER_LINE + " LPP: " + LINES_PER_PAGE);
        // run through the cols and rows of cards
        int baseX, baseY; //defined before loop to save processing time
        double line; //defined before loop to save processing time
        int numOnLine; //defined before loop to save processing time
        int labelNum = 0;
        //figure out which col and row we must start on based on page #
        double initialCard = ((double) pageIndex * LABELS_PER_PAGE)
                / MY_SEQUENCE.getGridSize().height;
        int initialC = (int) Math.ceil(initialCard);
        int initialR = (int) ((initialCard - Math.floor(initialCard))
                * MY_SEQUENCE.getGridSize().height);
        for (int c = initialC; c < MY_SEQUENCE.getGridSize().width; c++) {
            for (int r = initialR; r < MY_SEQUENCE.getGridSize().height; r++) {
                //calculate what line it is on by dividing and rounding up
                line = ((double) labelNum / LABELS_PER_LINE);
                //calc baseY based on line
                baseY = ((int) line) * (LABEL_HEIGHT + V_LABEL_GAP);
                //calc numOnLine based on the decimal portion of line
                numOnLine = (int) (LABELS_PER_LINE * (line - Math.floor(line)));
                //calc baseX based on numOnLine
                baseX = numOnLine * (LABEL_WIDTH + H_LABEL_GAP);
                //draw the line
                drawLabel(graphics, c, r, baseX, baseY);
                labelNum++;
            }
        }

        // tell the caller that this page is part
        // of the printed document
        return PAGE_EXISTS;
    }

    /**
     * Draws a label
     *
     * @param g the graphics object with which to draw
     * @param cardCol the column of the card
     * @param cardRow the row of the card
     * @param baseX the base x location at which to draw
     * @param baseY the base y location at which to draw
     */
    private void drawLabel(Graphics g, int cardCol, int cardRow, int baseX,
            int baseY) {
        //define the cardVals;
        int[] cardVals = MY_SEQUENCE.getCardSteps(cardCol, cardRow);
        //create a FontMetrics to measure the size of each value
        FontMetrics fm = g.getFontMetrics();
        //the x location of the next String relative to baseX
        int relX = 0;
        //the y location of the next String relative to baseY
        int relY = fm.getHeight();
        //write the card's row and column
        g.drawString("[" + cardCol + ", " + cardRow + "]", baseX + relX, baseY
                + relY);
        //increase relY
        relY += fm.getHeight();
        //set the color names up as a String[] to make using them easier
        String[] colorNames = {C1_NAME, C2_NAME};
        //write the values
        int valWidth; //initialized before loop to increase efficiency
        String val; //initialized before loop to increase efficiency
        for (int i = 0; i < cardVals.length; i++) {
            //determine the val
            val = String.valueOf(i + 1) + ":" + colorNames[cardVals[i]];
            //determine the width of the value
            valWidth = fm.stringWidth(val);
            //if value will go off the edge of the label, change relY
            if (relX + valWidth > LABEL_WIDTH) {
                relY += fm.getHeight();
                relX = 0;
            }
            //if the relY is below the bottom of the card, break
            if (relY > LABEL_HEIGHT) {
                break;
            }
            //write the value
            g.drawString(val, baseX + relX, baseY + relY);
            //increase relX
            relX += valWidth + VAL_GAP;
        }
    }

    /**
     * Prints the labels for a sequence
     *
     * @param seq the Sequence for which to print labels
     * @param c1Name the human-readable name of the color on the first side of
     * the card
     * @param c2Name the human-readable name of the color on the second side of
     * the card
     * @param hLabelGap the horizontal gap, in inches, between the labels
     * @param vLabelGap the vertical gap, in inches, between the labels
     * @param labelWidth the width, in inches, of one label
     * @param labelHeight the height, in inches of one label
     * @param labelsPerLine the number of labels in one row
     * @param linesPerPage the number of lines on one page
     */
    public static void printSequenceLabels(Sequence seq, String c1Name,
            String c2Name, double hLabelGap, double vLabelGap, double labelWidth,
            double labelHeight, int labelsPerLine, int linesPerPage) {
        //create the job
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new SequencePrinter(seq, c1Name, c2Name, hLabelGap,
                vLabelGap, labelWidth, labelHeight, labelsPerLine,
                linesPerPage));
        //show a dialog to print
        boolean doPrint = job.printDialog();
        //if the user said to print, print
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    public static int inchesToPixels(double inches){
        int pixels = (int) (inches*PIXELS_PER_INCH + .5);
        return pixels;
    }
}
