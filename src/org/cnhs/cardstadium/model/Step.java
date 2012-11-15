/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cnhs.cardstadium.model;

import java.awt.Dimension;
import org.cnhs.cardstadium.util.DeepCopy;

/**
 *
 * @author jcox
 * @version Nov 9, 2012
 */
public class Step {
    private String name;
    private int[][] cardVals;
    private int[][] backupVals;
    private Dimension size;

    public Step(Dimension size){
        this.name = "New Step";
        this.size = size;

    }

    public Step(String name, Dimension size){
        this.name = name;
        this.size = size;
    }

    public Step(String name, int[][] cardVals){
        this.name = name;
        this.size = new Dimension(cardVals.length, cardVals[0].length);
        this.cardVals = (int[][]) DeepCopy.copy(cardVals);
        this.backupVals = (int[][]) DeepCopy.copy(cardVals);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[][] getCardVals() {
        return cardVals;
    }

    public void setCardVals(int[][] cardVals) {
        this.cardVals = cardVals;
        backupCardVals();
    }

    public Dimension getSize(){
        return size;
    }

    public void setSize(Dimension size){
         //if the new size is bigger than backupSteps, increase its size
        if(size.width > backupVals.length || size.height
                > backupVals[0].length){
            resizeBackupVals(size);
        }
        //copy the step over to the backuo
        backupCardVals();
        //resize the step
        resizeCardVals(size);
        this.size = size;
    }

    /**
     * Redefines steps to be the new size. If the size increases and
     * is less than the backupStep size, it will restore cards from
     * backupStep
     * @param size the new size of the step
     * @return the newly resized step
     */
    private void resizeCardVals(Dimension size){
        int[][] newStep = new int[size.width][size.height];
        for(int c = 0; c < newStep.length; c++){
            for(int r = 0; r < newStep[c].length; r++){
                if(c < cardVals.length && r < cardVals[c].length){
                    newStep[c][r] = cardVals[c][r];
                } else if (c < backupVals.length && r < backupVals[c].length) {
                    newStep[c][r] = backupVals[c][r];
                } else {
                    newStep[c][r] = 0;
                }
            }
        }
        cardVals = newStep;
    }

    /**
     * Redefines backupVals to be the new size. If the size increases and
     * is less than the backupVals size, it will restore cards from
     * backupSteps
     * @param size the new size of the step
     */
    private void resizeBackupVals(Dimension size){
        int[][] newStep = new int[size.width][size.height];
        for(int c = 0; c < newStep.length; c++){
            for(int r = 0; r < newStep[c].length; r++){
                if(c < backupVals.length && r < backupVals[c].length){
                    newStep[c][r] = backupVals[c][r];
                } else {
                    newStep[c][r] = 0;
                }
            }
        }
        backupVals = newStep;
    }

    /**
     * Backs up the specified step so that any altered cards are rememberd in
     * backupSteps
     */
    private void backupCardVals(){
        for(int c = 0; c < cardVals.length; c++){
            for(int r = 0; r < cardVals[c].length; r++){
                backupVals[c][r] = cardVals[c][r];
            }
        }
    }

    @Override
    public String toString(){
        return name;
    }

}
