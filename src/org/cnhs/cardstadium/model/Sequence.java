/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.cnhs.cardstadium.gui.PerspectiveEditorPanel;
import org.cnhs.cardstadium.util.DeepCopy;

/**
 * Stores all the steps in a card show sequence
 *
 * @author jcox
 * @version Aug 23, 2012
 */
public class Sequence extends DefaultListModel{

    /**
     * The extension for a Sequence file
     */
    public final static String FILE_EXTENSION = "seq";
    private File myFile;
    private Color c1, c2;
    private Dimension gridSize;
//    private ArrayList<int[][]> steps = new ArrayList<int[][]>();
//    private ArrayList<Step> steps = new ArrayList<Step>();
    private ArrayList<int[][]> backupSteps = new ArrayList<int[][]>();


    /**
     * Creates a new sequence with default values from SequenceDefault.seq
     */
    public Sequence() {
        try {
            myFile = new File(getClass().getClassLoader().getResource(
                    "org/cnhs/cardstadium/model/SequenceDefault.seq").toURI());
        } catch (URISyntaxException ex) {
            ex.printStackTrace(System.err);
        }
        parseFile(myFile);

    }

    /**
     * Creates a Sequence with the data from a sequence File
     *
     * @param myFile the File from which this sequence should be loaded
     */
    public Sequence(File myFile) {
        this.myFile = myFile;
        parseFile(myFile);
    }

    /**
     * Creates a Sequence based on a file chosen with a JFileChooser.
     *
     * @param stadiumDisplay the JPanel in which this Sequence's Stadium will be
     * displayed
     * @return the Sequence loaded from the file
     */
    public static Sequence loadSequenceWithFileChooser(JPanel stadiumDisplay) {
        JFileChooser sfc = new JFileChooser();
        //only allow sfc to select a single file
        sfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        sfc.setMultiSelectionEnabled(false);
        //only accept files with the correct extension
        sfc.setFileFilter(new FileNameExtensionFilter(
                "Card Stadium Sequence file", FILE_EXTENSION));
        //choose a file
        int choice = sfc.showOpenDialog(null);
        //create the Stadium if they clicked "open"
        if (choice == JFileChooser.APPROVE_OPTION) {
            return new Sequence(sfc.getSelectedFile());
        } else {
            return null;
        }
    }

    /**
     * Adds a new step to this sequence at the desired index
     *
     * @param index which step this will be, starting with 0
     * @param step an int[][], where each card has an int (0 or 1) telling which
     * side of the card to show. Ex: step[1][0] = 1 means the card in column 1
     * and row 0 will show its second color
     */
    public void addStep(int index, int[][] step, String stepName) {
        this.insertElementAt(new Step(stepName, step), index);
        int[][] stepCopy = (int[][]) DeepCopy.copy(step);
        backupSteps.add(index, stepCopy);
    }

    /**
     * Adds a default step in which all cards on are side 1
     *
     * @param index the index at which to add the step, starting at 0
     */
    public void newStep(int index) {
        //define the step
        int[][] step  = new int[gridSize.width][gridSize.height];
        for(int c = 0; c < step.length; c++){
            for(int r = 0; r < step[c].length; r++){
                step[c][r] = 0; //use color 1 for all cards
            }
        }
        //add the step to the sequence
        addStep(index, step, "New Step");
    }

    /**
     * Deletes the step at the desired index
     *
     * @param index the index of the step, starting at 0
     */
    public void deleteStep(int index){
        if (index == 0) {
            if(this.getSize() == 1){
                newStep(0);
            } else if(this.getSize() > 1){
                this.insertElementAt(this.getElementAt(1), 0);
                this.removeElementAt(2);
            }
            index++;
        }
        this.removeElementAt(index);
    }

    public void flipColorsForStep(int stepNum){
        int[][] step = this.getStep(stepNum);
        for(int c = 0; c < step.length; c++){
            for(int r = 0; r < step[c].length; r++){
                step[c][r] = Math.abs(step[c][r] - 1);
            }
        }
    }

    public String getStepName(int index){
        return ((Step)(this.getElementAt(index))).getName();
    }

    /**
     * Gets all the step values for a certain card.
     *
     * @param cardCol the column in which the card is
     * @param cardRow the row in which the card is
     * @return an array of ints representing which side of the card is shown on
     * each step, or null if the card col or row is invalid
     */
    public int[] getCardSteps(int cardCol, int cardRow){
        //return null if the cardCol or cardRow is invalid
        if(cardCol < 0 || cardCol > gridSize.width || cardRow < 0
                || cardRow > gridSize.height){
            return null;
        }
        //run through steps and store all the values for the specified card
        int[] vals = new int[getNumSteps()];
        for(int i = 0; i < vals.length; i++){
            vals[i] = getStep(i)[cardCol][cardRow];
        }

        return vals;
    }

    /**
     * Changes which side of the card the user clicked on is shown
     *
     * @param pep the Perspective Editor Panel
     * @param mouseLoc the location of the click, relative to the parent panel
     * of the background
     * @param currentStep the step number which is being edited
     * @return true if a card has changed, false if none have changed (mouseLoc
     * was not over a card)
     */
    public boolean changeCardSide(PerspectiveEditorPanel pep, Point mouseLoc,
            int currentStep) {
        //create a polygon to see if the mouse click is within the grid of cards
        Polygon cardGrid = getPolygonForPoints(pep.getUpperLeftPoint(),
                pep.getUpperRightPoint(), pep.getLowerRightPoint(),
                pep.getLowerLeftPoint());
        //if the mouseLoc is not within cardGrid, return to save time
        if (!cardGrid.contains(mouseLoc)) {
            //return false;
        }
        //run through all of the polygons (cards) in the grid
        int col = -1; // the column the mouse is in
        int row = -1; // the row the mouse is in
        Polygon[][] cards = pep.getPolygonsForPerspectiveGrid();
        for (int c = 0; c < cards.length && col == -1; c++) {
            for (int r = 0; r < cards[c].length && row == -1; r++) {
                //if the mouse is in this card
                if (cards[c][r].contains(mouseLoc)) {
                    col = c;
                    row = r;
                }
            }
        }
        //if the mouseLoc is not in one of the cards, exit
        if (col == -1 || row == -1) {
            return false;
        }
        //change the value on the card
        this.getStep(currentStep)[col][row] =
                Math.abs(this.getStep(currentStep)[col][row] - 1);
        return true;
    }

    /**
     * Changes which side of the card the user clicked on is shown
     *
     * @param pep the Perspective Editor Panel
     * @param mouseLoc the location of the click, relative to the parent panel
     * of the background
     * @param currentStep the step number which is being edited
     * @return true if a card has changed, false if none have changed (mouseLoc
     * was not over a card)
     */
    public boolean changeCardSide(PerspectiveEditorPanel pep, Point mouseLoc,
            int currentStep, int color) {
        //create a polygon to see if the mouse click is within the grid of cards
        Polygon cardGrid = getPolygonForPoints(pep.getUpperLeftPoint(),
                pep.getUpperRightPoint(), pep.getLowerRightPoint(),
                pep.getLowerLeftPoint());
        //if the mouseLoc is not within cardGrid, return to save time
        if (!cardGrid.contains(mouseLoc)) {
            //return false;
        }
        //run through all of the polygons (cards) in the grid
        int col = -1; // the column the mouse is in
        int row = -1; // the row the mouse is in
        Polygon[][] cards = pep.getPolygonsForPerspectiveGrid();
        for (int c = 0; c < cards.length && col == -1; c++) {
            for (int r = 0; r < cards[c].length && row == -1; r++) {
                //if the mouse is in this card
                if (cards[c][r].contains(mouseLoc)) {
                    col = c;
                    row = r;
                }
            }
        }
        //if the mouseLoc is not in one of the cards, exit
        if (col == -1 || row == -1) {
            return false;
        }
        //change the value on the card
        this.getStep(currentStep)[col][row] = color;
        return true;
    }

    /**
     * Get the Color on the first side of each card.
     *
     * @return the Color on the first side of the card
     */
    public Color getC1() {
        return c1;
    }

    /**
     * Get the Color on the second side of each card.
     *
     * @return the Color on the second side of the card
     */
    public Color getC2() {
        return c2;
    }

    /**
     * Get the size of this Sequence's grid of cards.
     *
     * @return number of columns and rows of cards to be used for this Sequence,
     * stored as a Dimension (cols, rows)
     */
    public Dimension getGridSize() {
        return gridSize;
    }

    /**
     * Get a step in this Sequence.
     *
     * @param stepNum which step you want to get, starting at 0
     * @return an int[][], where each card has an int (0 or 1) telling which
     * side of the card to show. Ex: step[1][0] = 1 means the card in column 1
     * and row 0 will show its second color
     */
    public int[][] getStep(int stepNum) {
        return ((Step)(this.getElementAt(stepNum))).getCardVals();
    }

    /**
     * Gets how many steps the sequence currently has.
     *
     * @return the number of steps
     */
    public int getNumSteps() {
        return this.size();
    }

    /**
     * Get the file to which this sequence is saved.
     *
     * @return the File that stores this Sequence's info
     */
    public File getFile() {
        return myFile;
    }

    /**
     * Sets the Colors shown on both sides of the cards for this sequence
     *
     * @param c1 the Color on the first side
     * @param c2 the Color on the second side
     */
    public void setColors(Color c1, Color c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    /**
     * Sets how many columns and rows of cards will be used for this Sequence
     *
     * @param gridSize the columns and rows, stored as a Dimension (cols, rows)
     */
    public void setGridSize(Dimension gridSize) {
        //run through all of the steps
        for(int i = 0; i < this.getSize(); i++){
            ((Step)(this.getElementAt(i))).setSize(gridSize);
        }
        this.gridSize = gridSize;
    }

    /**
     * Saves all this Sequence's information to a file
     *
     * @param f the File to which it will save
     */
    public void saveToFile(File f) {
        myFile = f;
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(f, false)));
            //write the grid size
            bw.write(gridSize.width + " " + gridSize.height);
            bw.newLine();
            //write the rgb value for color 1
            bw.write(c1.getRed() + " " + c1.getBlue() + " " + c1.getGreen());
            bw.newLine();
            //write the rgb value for color 2
            bw.write(c2.getRed() + " " + c2.getBlue() + " " + c2.getGreen());
            bw.newLine();
            //write the total number of steps
            bw.write(String.valueOf(this.getSize()));
            bw.newLine();
            //write the steps
            for (int i = 0; i < this.getSize(); i++) {
                bw.write(this.getElementAt(i).toString());
                String step = "";
                //add all the colors to step
                for (int r = 0; r < gridSize.height; r++) { //rows
                    for (int c = 0; c < gridSize.width; c++) { //cols
                        step = step + this.getStep(i)[c][r];
                    }
                }
                //write the step to the file
                bw.write(step);
                bw.newLine();
            }
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }


    /**
     * Reads a File to determine all the information for this Sequence.
     *
     * @param file the File to be parsed
     */
    private void parseFile(File file) {
        myFile = file;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF8"));
            gridSize = parseGridSize(br.readLine()); //determine the grid size
            c1 = parseColor(br.readLine()); //determine the first color
            c2 = parseColor(br.readLine()); //determine the second color
            //determine how many steps are in this sequence
            int numSteps = Integer.parseInt(br.readLine());
            //loop through the lines containing the steps
            for (int i = 0; i < numSteps; i++) {
                String name = br.readLine();
                addStep(i, parseStep(br.readLine()), name);
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Parses a line of text to get all the values for 1 step of the Sequence
     *
     * @param line a line of text formatted as "intintintint ..."
     * @return an int[][], where each card has an int (0 or 1) telling which
     * side of the card to show. Ex: step[1][0] = 1 means the card in column 1
     * and row 0 will show its second color
     */
    private int[][] parseStep(String line) {
        //create an array to hold all the values
        int[][] vals = new int[gridSize.width][gridSize.height];
        //loop through the rows
        for (int r = 0; r < gridSize.height; r++) {
            //loop through the columns
            for (int c = 0; c < gridSize.width; c++) {
                vals[c][r] = Integer.parseInt(line.substring(0, 1));
                line = line.substring(1);
            }
        }
        return vals;
    }

    /**
     * Parses a line of text to determine the grid size
     *
     * @param line a line of text formatted as "cols rows"
     * @return the grid size as a Dimension
     */
    private Dimension parseGridSize(String line) {
        //parse num of cols
        int c = Integer.parseInt(line.substring(0, line.indexOf(" ")));
        //parse num of rows
        int r = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
        return new Dimension(c, r);
    }

    /**
     * Parses a color from a line of text
     *
     * @param line a line of text formatted as "r g b"
     * @return
     */
    private Color parseColor(String line) {
        //parse the red value
        int r = Integer.parseInt(line.substring(0, line.indexOf(" ")));
        //parse the green value
        int b = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.lastIndexOf(" ")));
        //parse the blue value
        int g = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1));
        return new Color(r, g, b);
    }

    /**
     * Create a polygon based on four points
     *
     * @param a point 1
     * @param b point 2
     * @param c point 3
     * @param d point 4
     * @return a polygon with a, b, c, d as the four corners
     */
    private Polygon getPolygonForPoints(Point a, Point b, Point c, Point d) {
        int[] xPoints = new int[]{a.x, b.x, c.x, d.x};
        int[] yPoints = new int[]{a.y, b.y, c.y, d.y};
        return new Polygon(xPoints, yPoints, 4);
    }

}
