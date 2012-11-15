/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import org.cnhs.cardstadium.gui.PerspectiveEditorPanel;
import org.cnhs.cardstadium.model.Stadium;

/**
 * 
 * @author pangle
 */
public class StadiumSyncUtil {
    public static void syncStadiumToEditor(Stadium s, PerspectiveEditorPanel e) {
        e.setHorizontalSubdivisions(s.getHorizontalSubdivisions());
        e.setLowerLeftPoint(s.getLowerLeftPoint());
        e.setLowerRightPoint(s.getLowerRightPoint());
        e.setSubdivisionGutterSize(s.getSubdivisionGutterSize());
        e.setUpperLeftPoint(s.getUpperLeftPoint());
        e.setUpperRightPoint(s.getUpperRightPoint());
        e.setVerticalSubdivisions(s.getVerticalSubdivisions());
    }
    
    public static void syncEditorToStadium(Stadium s, PerspectiveEditorPanel e) {
        s.setHorizontalSubdivisions(e.getHorizontalSubdivisions());
        s.setLowerLeftPoint(e.getLowerLeftPoint());
        s.setLowerRightPoint(e.getLowerRightPoint());
        s.setSubdivisionGutterSize(e.getSubdivisionGutterSize());
        s.setUpperLeftPoint(e.getUpperLeftPoint());
        s.setUpperRightPoint(e.getUpperRightPoint());
        s.setVerticalSubdivisions(e.getVerticalSubdivisions());
    }
}
