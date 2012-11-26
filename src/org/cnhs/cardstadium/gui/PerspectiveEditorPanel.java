/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.cnhs.cardstadium.util.DeepCopy;
import org.cnhs.cardstadium.util.PointUtil;

/**
 * A grid similar to the Perspective Crop in Adobe Photoshop CS6. Essential
 * purpose is to generate polygons within the gridded squares. Always place this
 * component inside a parent JPanel so that the origin of this panel is [0, 0]
 * relative to the parent.
 *
 * @author pangle
 * @version 9/9/12
 * @since 9/9/12
 */
public class PerspectiveEditorPanel extends JPanel implements MouseMotionListener, MouseListener {

    private static final int RESIZE_HANDLE_SIZE = 7;
    
    private static final float[] PRIMARY_DASH_PATTERN = {5.0f};
    private static final float[] SECONDARY_DASH_PATTERN = {5.0f};
    private static final float[] TRINARY_DASH_PATTERN = {2.5f};
    
    private static final int MOUSEEVENT_X_OFFSET = 0;//-12;
    private static final int MOUSEEVENT_Y_OFFSET = 0;//-80;//-30;
    
    private Point upperLeftPoint, upperRightPoint, lowerLeftPoint, lowerRightPoint;
    private int verticalSubdivisions, horizontalSubdivisions;
    private double subdivisionGutterSize; // Double as percent of total
    
    private double scale = 1;
    
    private Point selectedCornerPoint = null;
    
    private float dashPhase = 0;
    private boolean validGrid = true;
    
    private boolean cacheIsValid = false;
    private Polygon[][] cachedPolygons;
    private Polygon[][] givenPolygons;

    /**
     * Create a new PerspectiveEditorPanel with default values JavaBean
     * Compatible Constructor
     */
    public PerspectiveEditorPanel() {
        this.upperLeftPoint = new Point(20, 20);
        this.upperRightPoint = new Point(400, 40);
        this.lowerLeftPoint = new Point(20, 420);
        this.lowerRightPoint = new Point(440, 420);
        this.verticalSubdivisions = 8;
        this.horizontalSubdivisions = 12;
        this.subdivisionGutterSize = 0.01;

        Timer paintTimer = new Timer(100, null);
        paintTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (isVisible()) {
                    ++dashPhase;
                    repaint();
                }
            }
        });
        paintTimer.start();
    }

    /**
     * Boiler plate polygon return. TODO: Implement actual code.
     *
     * @return Polygon[][] in order [x][y] of the polygons formed.
     */
    public Polygon[][] getPolygonsForPerspectiveGrid() {
        if (!cacheIsValid) {
            cachedPolygons = new Polygon[this.getVerticalSubdivisions()][this.getHorizontalSubdivisions()];

            for (int col = 0; col < this.getVerticalSubdivisions(); col++) {
                double colStart = ((double) col) / ((double) getVerticalSubdivisions());
                double colEnd = ((double) col + 1.0) / ((double) getVerticalSubdivisions());

                double colPolyStart = ((double) colStart) + ((double) subdivisionGutterSize);
                double colPolyEnd = ((double) colEnd) - ((double) subdivisionGutterSize);

//                Point leftLineOrigin = getPointOnLine(upperLeftPoint, upperRightPoint, colStart);
//                Point leftLineEnd = getPointOnLine(lowerLeftPoint, lowerRightPoint, colStart);
//
//                Point rightLineOrigin = getPointOnLine(upperLeftPoint, upperRightPoint, colEnd);
//                Point rightLineEnd = getPointOnLine(lowerLeftPoint, lowerRightPoint, colEnd);

                for (int row = 0; row < this.getHorizontalSubdivisions(); row++) {
                    double rowStart = ((double) row) / ((double) getHorizontalSubdivisions());
                    double rowEnd = ((double) row + 1.0) / ((double) getHorizontalSubdivisions());

                    double rowPolyStart = ((double) rowStart) + ((double) subdivisionGutterSize);
                    double rowPolyEnd = ((double) rowEnd) - ((double) subdivisionGutterSize);

                    Point topLineOrigin = getPointOnLine(upperLeftPoint, lowerLeftPoint, rowPolyStart);
                    Point topLineEnd = getPointOnLine(upperRightPoint, lowerRightPoint, rowPolyStart);

                    Point bottomLineOrigin = getPointOnLine(upperLeftPoint, lowerLeftPoint, rowPolyEnd);
                    Point bottomLineEnd = getPointOnLine(upperRightPoint, lowerRightPoint, rowPolyEnd);
                    

                    cachedPolygons[col][row] = this.getPolygonForPoints(
                            getPointOnLine(topLineOrigin, topLineEnd, colPolyStart),
                            getPointOnLine(topLineOrigin, topLineEnd, colPolyEnd),
                            getPointOnLine(bottomLineOrigin, bottomLineEnd, colPolyEnd),
                            getPointOnLine(bottomLineOrigin, bottomLineEnd, colPolyStart)
                            );
                }
            }
            
            cacheIsValid = true;
        }
        
        givenPolygons = null;
        System.gc();
        givenPolygons = new Polygon[this.getVerticalSubdivisions()][this.getHorizontalSubdivisions()];
        
        // GO COPY!
        givenPolygons = (Polygon[][]) DeepCopy.copy(cachedPolygons);
        
        return givenPolygons;
    }
    
    public void invalidateCache() {
        cacheIsValid = false;
    }
    
    /**
     * 
     */
    public void constrainPointsWithinBounds(int xMax, int yMax) {
        Point[] cornerPoints = {upperLeftPoint, upperRightPoint, lowerLeftPoint, lowerRightPoint};
        
        for (Point p : cornerPoints) {
            if (p.x < 0) {
                p.x = 0;
            }
            
            if (p.x > xMax) {
                p.x = xMax;
            }
            
            if (p.y < 0) {
                p.y = 0;
            }
            
            if (p.y > yMax) {
                p.y = yMax;
            }
        }
        
        invalidateCache();
    }
    
    
    /**
     * Create a four-sided polygon from the given four points. Points should be
     * defined circularly.
     *
     * @param a Point
     * @param b Point
     * @param c Point
     * @param d Point.
     * @return Polygon from the four provided points
     */
    private Polygon getPolygonForPoints(Point a, Point b, Point c, Point d) {
        int[] xPoints = new int[]{a.x, b.x, c.x, d.x};
        int[] yPoints = new int[]{a.y, b.y, c.y, d.y};
        return new Polygon(xPoints, yPoints, 4);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        
        
        if (pointsAreValid()) {
            g2.setColor(Color.GRAY);
        } else {
            g2.setColor(Color.RED);
            g2.drawString("Error: A corner can not be within the area formed by the other corners", 5, 15);
            g2.setColor(Color.CYAN);
        }
        g2.setXORMode(Color.BLACK);
        //this.paintAllTrinarys(g2);
        this.paintAllSecondarys(g2);
        this.paintAllPrimarys(g2);
        this.paintAllHandles(g2);
    }

    /**
     * Paint a corner handle
     *
     * @param g Graphics2D object for drawing
     * @param handleX int of x location of handle center
     * @param handleY int of y location of handle center
     * @param handleSize int of width and height of handle
     */
    private void paintHandle(Graphics2D g, int handleX, int handleY, int handleSize) {
        Point center = PointUtil.scalePoint(new Point(handleX, handleY), scale);
        handleX = center.x;
        handleY = center.y;
        
        g.fillRect(handleX - (handleSize / 2), handleY - (handleSize / 2), handleSize, handleSize);
    }

    /**
     * Paint a primary style line between the given points
     *
     * @param g Graphics2D object for drawing
     * @param origin Point
     * @param destination Point
     */
    private void paintPrimary(Graphics2D g, Point origin, Point destination) {
        origin = PointUtil.scalePoint(origin, scale);
        destination = PointUtil.scalePoint(destination, scale);
        
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, PRIMARY_DASH_PATTERN[0], PRIMARY_DASH_PATTERN, dashPhase));
        g.drawLine(origin.x, origin.y, destination.x, destination.y);
        g.setStroke(new BasicStroke());
    }

    /**
     * Paint a secondary style line between the given points
     *
     * @param g Graphics2D object for drawing
     * @param origin Point
     * @param destination Point
     */
    private void paintSecondary(Graphics2D g, Point origin, Point destination) {
        origin = PointUtil.scalePoint(origin, scale);
        destination = PointUtil.scalePoint(destination, scale);
        
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, SECONDARY_DASH_PATTERN[0], SECONDARY_DASH_PATTERN, dashPhase));
        g.drawLine(origin.x, origin.y, destination.x, destination.y);
        g.setStroke(new BasicStroke());
    }

    /**
     * Paint a trinary style line between the given points
     *
     * @param g Graphics2D object for drawing
     * @param origin Point
     * @param destination Point
     */
    private void paintTrinary(Graphics2D g, Point origin, Point destination) {
        origin = PointUtil.scalePoint(origin, scale);
        destination = PointUtil.scalePoint(destination, scale);
        
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, TRINARY_DASH_PATTERN[0], TRINARY_DASH_PATTERN, dashPhase));
        g.drawLine(origin.x, origin.y, destination.x, destination.y);
        g.setStroke(new BasicStroke());
    }

    /**
     * Paint the four corner handles
     *
     * @param g Graphics2D object for drawing
     */
    private void paintAllHandles(Graphics2D g) {
        this.paintHandle(g, upperLeftPoint.x, upperLeftPoint.y, RESIZE_HANDLE_SIZE);
        this.paintHandle(g, upperRightPoint.x, upperRightPoint.y, RESIZE_HANDLE_SIZE);
        this.paintHandle(g, lowerLeftPoint.x, lowerLeftPoint.y, RESIZE_HANDLE_SIZE);
        this.paintHandle(g, lowerRightPoint.x, lowerRightPoint.y, RESIZE_HANDLE_SIZE);
    }

    /**
     * Paint the four primary lines
     *
     * @param g Graphics2D object for drawing
     */
    private void paintAllPrimarys(Graphics2D g) {
        this.paintPrimary(g, upperLeftPoint, upperRightPoint);
        this.paintPrimary(g, upperRightPoint, lowerRightPoint);
        this.paintPrimary(g, lowerRightPoint, lowerLeftPoint);
        this.paintPrimary(g, lowerLeftPoint, upperLeftPoint);
    }

    /**
     * Paint all of the secondary lines
     *
     * @param g Graphics2D object for drawing
     */
    private void paintAllSecondarys(Graphics2D g) {
        // Horizontals
        for (int row = 1; row < getHorizontalSubdivisions(); row++) {
            double percentOnLine = ((double) row) / ((double) getHorizontalSubdivisions());
            paintSecondary(g,
                    getPointOnLine(upperLeftPoint, lowerLeftPoint, percentOnLine),
                    getPointOnLine(upperRightPoint, lowerRightPoint, percentOnLine));
        }

        // Verticals
        for (int col = 1; col < getVerticalSubdivisions(); col++) {
            double percentOnLine = ((double) col) / ((double) getVerticalSubdivisions());
            paintSecondary(g,
                    getPointOnLine(upperLeftPoint, upperRightPoint, percentOnLine),
                    getPointOnLine(lowerLeftPoint, lowerRightPoint, percentOnLine));
        }
    }

    /**
     * Paint all of the trinary lines
     *
     * @param g Graphics2D object for drawing
     */
    private void paintAllTrinarys(Graphics2D g) {
        // Horizontals
        for (int row = 0; row < getHorizontalSubdivisions(); row++) {
            double percentOnLine = ((double) row) / ((double) getHorizontalSubdivisions());
            paintTrinary(g,
                    getPointOnLine(upperLeftPoint, lowerLeftPoint, percentOnLine + subdivisionGutterSize),
                    getPointOnLine(upperRightPoint, lowerRightPoint, percentOnLine + subdivisionGutterSize));
            percentOnLine = ((double) row + 1.0) / ((double) getHorizontalSubdivisions());
            paintTrinary(g,
                    getPointOnLine(upperLeftPoint, lowerLeftPoint, percentOnLine - subdivisionGutterSize),
                    getPointOnLine(upperRightPoint, lowerRightPoint, percentOnLine - subdivisionGutterSize));
        }

        // Verticals
        for (int col = 0; col < getVerticalSubdivisions(); col++) {
            double percentOnLine = ((double) col) / ((double) getVerticalSubdivisions());
            paintTrinary(g,
                    getPointOnLine(upperLeftPoint, upperRightPoint, percentOnLine + subdivisionGutterSize),
                    getPointOnLine(lowerLeftPoint, lowerRightPoint, percentOnLine + subdivisionGutterSize));
            percentOnLine = ((double) col + 1.0) / ((double) getVerticalSubdivisions());
            paintTrinary(g,
                    getPointOnLine(upperLeftPoint, upperRightPoint, percentOnLine - subdivisionGutterSize),
                    getPointOnLine(lowerLeftPoint, lowerRightPoint, percentOnLine - subdivisionGutterSize));
        }
    }

    /**
     * Checks if the two integers are within range of each other
     *
     * @param a int
     * @param b int
     * @param range int
     * @return
     */
    private boolean isWithinRange(int a, int b, int range) {
        return (Math.abs(a - b) <= range);
    }

    /**
     * Get the length of a line between the two given points
     *
     * @param a Point
     * @param b Point
     * @return
     */
    private double getLineLength(Point a, Point b) {
        return Math.hypot(b.x - a.x, b.y - a.y);
    }

    private boolean pointsAreValid() {
        Point[] cornerPoints = {upperLeftPoint, upperRightPoint, lowerLeftPoint, lowerRightPoint};

        for (int i = 0; i < cornerPoints.length; i++) {
            Polygon p = new Polygon(new int[]{cornerPoints[(i + 1) % 4].x, cornerPoints[(i + 2) % 4].x, cornerPoints[(i + 3) % 4].x}, new int[]{cornerPoints[(i + 1) % 4].y, cornerPoints[(i + 2) % 4].y, cornerPoints[(i + 3) % 4].y}, 3);
            if (p.contains(cornerPoints[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get a new point on the line given by the two points that is percentOnLine
     * down the line from a
     *
     * @param a Point
     * @param b Point
     * @param percentOnLine double representing a percent 0.0000 to 1.0000 (0%
     * to 100%)
     * @return
     */
    private Point getPointOnLine(Point a, Point b, double percentOnLine) {
        // Find length of line and delta values of x and y
        double length = this.getLineLength(a, b);
        double dX = b.x - a.x;
        double dY = (b.y - this.getHeight()) - (a.y - this.getHeight());

        // Calculate the angle of the origin about the vector towards the second point
        double radians = Math.atan2(dY, dX);

        // Calculate new points with polar to cartesian conversion
        double newX = ((length * percentOnLine) * Math.cos(radians)) + a.x;
        double newY = ((length * percentOnLine) * Math.sin(radians)) + a.y;

        // Create the new point by combining x and y values.
        return new Point((int) newX, (int) newY);
    }

    /**
     * Check if the given point is within range (accuracy) of
     *
     * @param comparitor
     * @param accuracy
     * @return
     */
    private Point mouseIsOverPoint(Point comparitor, int accuracy) {
        Point[] cornerPoints = {upperLeftPoint, upperRightPoint, lowerLeftPoint, lowerRightPoint};
        for (Point c : cornerPoints) {
            if (isWithinRange(c.x, comparitor.x, accuracy) && isWithinRange(c.y, comparitor.y, accuracy)) {
                return c;
            }
        }
        return null;
    }

    /**
     *
     * @param location
     */
    private void setSelectedPointPosition(Point location) {
        Point backup;
        if (selectedCornerPoint.equals(lowerLeftPoint)) {
            backup = lowerLeftPoint;
            lowerLeftPoint = location;
            if (!pointsAreValid()) {
                lowerLeftPoint = backup;
            }
        } else if (selectedCornerPoint.equals(lowerRightPoint)) {
            backup = lowerRightPoint;
            lowerRightPoint = location;
            if (!pointsAreValid()) {
                lowerRightPoint = backup;
            }
        } else if (selectedCornerPoint.equals(upperLeftPoint)) {
            backup = upperLeftPoint;
            upperLeftPoint = location;
            if (!pointsAreValid()) {
                upperLeftPoint = backup;
            }
        } else if (selectedCornerPoint.equals(upperRightPoint)) {
            backup = upperRightPoint;
            upperRightPoint = location;
            if (!pointsAreValid()) {
                upperRightPoint = backup;
            }
        }
        cacheIsValid = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point cursorPosition = e.getPoint();
        cursorPosition.translate(MOUSEEVENT_X_OFFSET, MOUSEEVENT_Y_OFFSET);
        cursorPosition = PointUtil.descalePoint(cursorPosition, scale);

        if (selectedCornerPoint != null) {
            this.setSelectedPointPosition(cursorPosition);
            selectedCornerPoint = mouseIsOverPoint(cursorPosition, RESIZE_HANDLE_SIZE * 2);
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point cursorPosition = e.getPoint();
        cursorPosition.translate(MOUSEEVENT_X_OFFSET, MOUSEEVENT_Y_OFFSET);
        cursorPosition = PointUtil.descalePoint(cursorPosition, scale);

        selectedCornerPoint = mouseIsOverPoint(cursorPosition, RESIZE_HANDLE_SIZE * 2);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point cursorPosition = e.getPoint();
        cursorPosition.translate(MOUSEEVENT_X_OFFSET, MOUSEEVENT_Y_OFFSET);
        cursorPosition = PointUtil.descalePoint(cursorPosition, scale);

        selectedCornerPoint = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     *
     * @return
     */
    public Point getUpperLeftPoint() {
        return upperLeftPoint;
    }

    /**
     *
     * @param upperLeftPoint
     */
    public void setUpperLeftPoint(Point upperLeftPoint) {
        this.upperLeftPoint = upperLeftPoint;
    }

    /**
     *
     * @return
     */
    public Point getUpperRightPoint() {
        return upperRightPoint;
    }

    /**
     *
     * @param upperRightPoint
     */
    public void setUpperRightPoint(Point upperRightPoint) {
        this.upperRightPoint = upperRightPoint;
    }

    /**
     *
     * @return
     */
    public Point getLowerLeftPoint() {
        return lowerLeftPoint;
    }

    /**
     *
     * @param lowerLeftPoint
     */
    public void setLowerLeftPoint(Point lowerLeftPoint) {
        this.lowerLeftPoint = lowerLeftPoint;
    }

    /**
     *
     * @return
     */
    public Point getLowerRightPoint() {
        return lowerRightPoint;
    }

    /**
     *
     * @param lowerRightPoint
     */
    public void setLowerRightPoint(Point lowerRightPoint) {
        this.lowerRightPoint = lowerRightPoint;
    }

    /**
     *
     * @return
     */
    public int getHorizontalSubdivisions() {
        return horizontalSubdivisions;
    }

    /**
     *
     * @param horizontalSubdivisions
     */
    public void setHorizontalSubdivisions(int horizontalSubdivisions) {
        this.horizontalSubdivisions = horizontalSubdivisions;
    }

    /**
     *
     * @return
     */
    public double getSubdivisionGutterSize() {
        return subdivisionGutterSize;
    }

    /**
     *
     * @param subdivisionGutterSize
     */
    public void setSubdivisionGutterSize(double subdivisionGutterSize) {
        this.subdivisionGutterSize = subdivisionGutterSize;
    }

    /**
     *
     * @return
     */
    public int getVerticalSubdivisions() {
        return verticalSubdivisions;
    }

    /**
     *
     * @param verticalSubdivisions
     */
    public void setVerticalSubdivisions(int verticalSubdivisions) {
        this.verticalSubdivisions = verticalSubdivisions;
    }

    public void setScale(double scale) {
        this.scale = scale;
        repaint();
    }
    
    
}