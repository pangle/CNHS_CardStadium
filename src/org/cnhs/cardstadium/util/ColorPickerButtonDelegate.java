/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.Color;

/**
 *
 * @author pangle
 */
public interface ColorPickerButtonDelegate {
    public abstract void respondToColorChange(Object sender, Color c);
}
