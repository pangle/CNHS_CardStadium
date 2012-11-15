/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.Point;

/**
 *
 * @author workstation
 */
public class PointUtil {
    public static Point scalePoint(Point p, double scale) {
        return new Point((int)((double)p.x * scale), (int)((double)p.y * scale));
    }
    
    public static Point descalePoint(Point p, double scale) {
        return new Point((int)((double)p.x / scale), (int)((double)p.y / scale));
    }
}
