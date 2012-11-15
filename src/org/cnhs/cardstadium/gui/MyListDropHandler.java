package org.cnhs.cardstadium.gui;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;

/*
 * http://www.java2s.com/Code/Java/Swing-JFC/Usedraganddroptoreorderalist.htm
 */

class MyListDropHandler extends TransferHandler {
  DragDropList list;

  public MyListDropHandler(DragDropList list) {
    this.list = list;
  }

    @Override
  public boolean canImport(TransferHandler.TransferSupport support) {
    if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      return false;
    }
    JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
    if (dl.getIndex() == -1) {
      return false;
    } else {
      return true;
    }
  }

    @Override
  public boolean importData(TransferHandler.TransferSupport support) {
    if (!canImport(support)) {
      return false;
    }

    Transferable transferable = support.getTransferable();
    String indexString;
    try {
      indexString = (String) transferable.getTransferData(DataFlavor.stringFlavor);
    } catch (Exception e) {
      return false;
    }

    int index = Integer.parseInt(indexString);
    JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
    int dropTargetIndex = dl.getIndex();


    ((DefaultListModel)(list.getModel())).insertElementAt(((DefaultListModel)
            (list.getModel())).getElementAt(index), dropTargetIndex);
    list.setSelectedIndex(dropTargetIndex);
    if(dropTargetIndex <= index){
        ((DefaultListModel)(list.getModel())).remove(index + 1);
    } else {
        ((DefaultListModel)(list.getModel())).remove(index);
    }

    return true;
  }
}