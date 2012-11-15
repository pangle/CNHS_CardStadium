package org.cnhs.cardstadium.gui;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

/*
 * http://www.java2s.com/Code/Java/Swing-JFC/Usedraganddroptoreorderalist.htm
 */
public class DragDropList extends JList {
    private MyDragListener dragListener;

    public DragDropList() {
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);

        setTransferHandler(new MyListDropHandler(this));

        dragListener = new MyDragListener(this);
    }
}

