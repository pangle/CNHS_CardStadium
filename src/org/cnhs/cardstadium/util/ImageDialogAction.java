/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author pangle
 */
public interface ImageDialogAction {
    public abstract void imageWasLoaded(BufferedImage i, File f);
    public abstract void imageWasNotLoaded();
}
