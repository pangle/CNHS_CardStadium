package org.cnhs.cardstadium.gui;


import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import org.cnhs.cardstadium.gui.DragDropList;

/*
 * http://www.java2s.com/Code/Java/Swing-JFC/Usedraganddroptoreorderalist.htm
 */

class MyDragListener implements DragSourceListener, DragGestureListener {

    DragDropList list;
    DragSource ds = new DragSource();

    public MyDragListener(DragDropList list) {
        this.list = list;
        DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(list,
                DnDConstants.ACTION_MOVE, this);

    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        StringSelection transferable = new StringSelection(Integer.toString(list.getSelectedIndex()));
        ds.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
//        if (dsde.getDropSuccess()) {
//            System.out.println("Succeeded");
//        } else {
//            System.out.println("Failed");
//        }
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }
}