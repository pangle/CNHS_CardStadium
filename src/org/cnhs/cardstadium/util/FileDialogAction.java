/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnhs.cardstadium.util;

import java.io.File;

/**
 *
 * @author pangle
 */
public interface FileDialogAction {
    public abstract void fileWasSelected( File f);
    public abstract void fileWasNotSelected();
}
