/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.model;

import java.awt.Dimension;
import java.awt.Point;
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
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A class used to store all the data about a stadium.
 *
 * @author jcox
 * @version Aug 22, 2012
 */
public class Stadium {

    /**
     * The file extension for a Stadium file
     */
    public final static String FILE_EXTENSION = "sta";
    private File myFile;
    private File stadiumBGFile;
    //perspective variables
    private Point upperLeftPoint, upperRightPoint, lowerLeftPoint, lowerRightPoint;
    private int verticalSubdivisions, horizontalSubdivisions;
    private double subdivisionGutterSize; // Double as percent of total
    private ArrayList<Sequence> mySequences = new ArrayList<Sequence>();

    /**
     * Creates a new Stadium using default values from StadiumDefault.sta
     */
    public Stadium() {
        try {
            myFile = new File(getClass().getClassLoader().getResource(
                    "org/cnhs/cardstadium/model/StadiumDefault.sta").toURI());
        } catch (URISyntaxException ex) {
            ex.printStackTrace(System.err);
        }
        parseFile(myFile);
    }

    /**
     * Create a new Stadium based on a stadium file
     *
     * @param myFile a File containing all the data about the stadium
     */
    public Stadium(File myFile) {
        this.myFile = myFile;
        parseFile(myFile);
    }

    /**
     * Creates a stadium based on a file chosen with a JFileChooser.
     *
     * @return the Stadium loaded from the file
     */
    public static Stadium loadStadiumWithFileChooser() {
        JFileChooser sfc = new JFileChooser();
        //only allow sfc to select a single file
        sfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        sfc.setMultiSelectionEnabled(false);
        //only accept files with the correct extension
        sfc.setFileFilter(new FileNameExtensionFilter(
                "Card Stadium Stadium file", FILE_EXTENSION));
        //choose a file
        int choice = sfc.showOpenDialog(null);
        //create the Stadium if they clicked "open"
        if (choice == JFileChooser.APPROVE_OPTION) {
            return new Stadium(sfc.getSelectedFile());
        } else {
            return null;
        }
    }

    /**
     * Associates a Sequence with this Stadium
     *
     * @param s the Sequence to be added
     */
    public void addSequence(Sequence s) {
        mySequences.add(s);
    }

    /**
     * Get the file for the background image of this Stadium.
     *
     * @return the photo of the stadium. If the path of the file is "DEFAULT",
     * the stadium uses the default background
     */
    public File getStadiumBGFile() {
        return stadiumBGFile;
    }

    /**
     * Get the File to which this Stadium is stored.
     *
     * @return the File that stores this Stadium's info
     */
    public File getFile() {
        return myFile;
    }

    /**
     * Gets the upper left point of the perspective grid
     *
     * @return the upper left Point
     */
    public Point getUpperLeftPoint() {
        return upperLeftPoint;
    }

    /**
     * Set the upper left point of the perspective grid
     *
     * @param upperLeftPoint
     */
    public void setUpperLeftPoint(Point upperLeftPoint) {
        this.upperLeftPoint = upperLeftPoint;
    }

    /**
     * Gets the upper right point of the perspective grid
     *
     * @return the upper right Point
     */
    public Point getUpperRightPoint() {
        return upperRightPoint;
    }

    /**
     * Set the upper left point of the perspective grid
     *
     * @param upperLeftPoint
     */
    public void setUpperRightPoint(Point upperRightPoint) {
        this.upperRightPoint = upperRightPoint;
    }

    /**
     * Gets the lower left point of the perspective grid
     *
     * @return the lower left Point
     */
    public Point getLowerLeftPoint() {
        return lowerLeftPoint;
    }

    /**
     * Set the lower left point of the perspective grid
     *
     * @param lowerLeftPoint
     */
    public void setLowerLeftPoint(Point lowerLeftPoint) {
        this.lowerLeftPoint = lowerLeftPoint;
    }

    /**
     * Gets the lower right point of the perspective grid
     *
     * @return the lower right Point
     */
    public Point getLowerRightPoint() {
        return lowerRightPoint;
    }

    /**
     * Set the lower right point of the perspective grid
     *
     * @param lowerRightPoint
     */
    public void setLowerRightPoint(Point lowerRightPoint) {
        this.lowerRightPoint = lowerRightPoint;
    }

    /**
     * Get the number of columns in the perspective grid
     *
     * @return the int number of columns
     */
    public int getVerticalSubdivisions() {
        return verticalSubdivisions;
    }

    /**
     * Sets the number of columns in the perspective grid
     *
     * @param verticalSubdivisions
     */
    public void setVerticalSubdivisions(int verticalSubdivisions) {
        this.verticalSubdivisions = verticalSubdivisions;
    }

    /**
     * Get the number of rows in the perspective grid
     *
     * @return the int number of rows
     */
    public int getHorizontalSubdivisions() {
        return horizontalSubdivisions;
    }

    /**
     * Sets the number of rows in the perspective grid
     *
     * @param horizontalSubdivisions
     */
    public void setHorizontalSubdivisions(int horizontalSubdivisions) {
        this.horizontalSubdivisions = horizontalSubdivisions;
    }

    /**
     * Get the padding between the polygons on the perspective grid
     *
     * @return the padding as a double
     */
    public double getSubdivisionGutterSize() {
        return subdivisionGutterSize;
    }

    /**
     * Set the padding between the polygons on the perspective grid
     *
     * @param subdivisionGutterSize
     */
    public void setSubdivisionGutterSize(double subdivisionGutterSize) {
        this.subdivisionGutterSize = subdivisionGutterSize;
    }

    /**
     * Sets the picture of the stadium that is in the background.
     *
     * @param stadiumBG a photo of the stadium. Pass a file with the path
     * "DEFAULT" to make the stadium use the default image.
     */
    public void setStadiumBGFile(File stadiumBGFile) {
        this.stadiumBGFile = stadiumBGFile;
    }

    /**
     * Saves all this Stadium's information to a file
     *
     * @param f the File to which it will save
     */
    public void saveToFile(File f) {
        myFile = f;
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(f, false)));
            //write the file path of stadiumBG
            bw.write(stadiumBGFile.getPath());
            bw.newLine();
            //write the perspective variables
            bw.write(upperLeftPoint.x + " " + upperLeftPoint.y);
            bw.newLine();
            bw.write(upperRightPoint.x + " " + upperRightPoint.y);
            bw.newLine();
            bw.write(lowerLeftPoint.x + " " + lowerLeftPoint.y);
            bw.newLine();
            bw.write(lowerRightPoint.x + " " + lowerRightPoint.y);
            bw.newLine();
            bw.write(verticalSubdivisions + " " + horizontalSubdivisions);
            bw.newLine();
            bw.write(String.valueOf(subdivisionGutterSize));
            bw.newLine();
            //write the total number of Sequences designed for this stadium
            bw.write(String.valueOf(mySequences.size()));
            bw.newLine();
            //write the Sequence file paths
            for (int i = 0; i < mySequences.size(); i++) {
                bw.write(mySequences.get(i).getFile().getPath());
                bw.newLine();
            }
            bw.close();
        } catch (IOException ex) {
            System.err.println("saveToFile() : " + ex);
        }
    }

    /**
     * Parses a File for all of the values required by a Stadium
     *
     * @param file the File to be parsed
     */
    private void parseFile(File file) {
        myFile = file;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF8"));
            //creat the stadiumBGFile
            stadiumBGFile = new File(br.readLine());
            //parse the corner points
            upperLeftPoint = parsePoint(br.readLine());
            upperRightPoint = parsePoint(br.readLine());
            lowerLeftPoint = parsePoint(br.readLine());
            lowerRightPoint = parsePoint(br.readLine());
            //parse the num of subdivisions as a dimension
            Dimension subdivisions = parseDimension(br.readLine());
            //store vertical and horizontal subdivisions
            verticalSubdivisions = subdivisions.width;
            horizontalSubdivisions = subdivisions.height;
            //parse subdivisionGutterSize
            subdivisionGutterSize = Double.parseDouble(br.readLine());
            //parse the files for sequences
            int num = Integer.parseInt(br.readLine());
            for (int i = 0; i < num; i++) {
                //addSequence(new Sequence(new File(br.readLine())));
            }
            br.close();
        } catch (IOException ex) {
            //System.err.println("parseFile() : " + ex);
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Parses a Dimension out of a line of text
     *
     * @param line the text to be parsed, formated as "width height"
     * @return the Dimension
     */
    private Dimension parseDimension(String line) {
        //parse num of cols
        int c = Integer.parseInt(line.substring(0, line.indexOf(" ")));
        //parse num of rows
        int r = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
        return new Dimension(c, r);
    }

    /**
     * Parses a point from a line of text
     *
     * @param line a String formatted as "x y"
     * @return a point representing the String
     */
    private Point parsePoint(String line) {
        //parse the line as a dimension
        Dimension d = parseDimension(line);
        //convert d to a point and return
        return new Point(d.width, d.height);
    }
}
