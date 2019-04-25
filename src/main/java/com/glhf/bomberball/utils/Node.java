package com.glhf.bomberball.utils;

import com.glhf.bomberball.maze.cell.Cell;

import java.util.ArrayList;

public class Node {

    private ArrayList<Cell> ancestors;
    // Node[0] for UP, 1 for RIGHT, 2 for DOWN, 3 for LEFT
    private Node[] sons;
    private Cell matching_cell;
    //NEW
    private Node ancestor;
    private int way_size;
    private int son_set;
    //

    public Node(ArrayList<Cell> ancestors, Cell matching_cell){
        this.ancestors = ancestors;
        this.matching_cell = matching_cell;
        this.sons = new Node[4];
    }

    //NEW
    public Node(ArrayList<Cell> ancestors, Node ancestor, Cell matching_cell) {
        this.ancestors = ancestors;
        this.matching_cell = matching_cell;
        this.sons = new Node[4];
        this.ancestor = ancestor;
        this.way_size = -1;
        this.son_set = 0;
    }
    //


    public void setSons(Node son, Directions dir){
        this.sons[dir.ordinal()] = son;
    }

    public ArrayList<Cell> getAncestors(){
        return ancestors;
    }

    public Cell getMatching_cell(){
        return matching_cell;
    }

    public Node getSons(int index){return  sons[index];}

    //NEW
    public int getWay_size() {
        return way_size;
    }

    private void setWay_size() {
        int max = 0;
        int max_size = 0;
        int i;
        for (i=0; i<4; i++) {
            if (sons[i] != null && sons[i].getWay_size() > max_size) {
                max_size = sons[i].getWay_size();
                max = i;
            }
        }
        for (i=0; i<4; i++) {
            if (i != max) {
                this.setSons(null, Directions.values()[i]);
            }
        }
        this.way_size = max_size + 1;
    }

    public void sonIsSet() {
        son_set++;
        if (son_set == 4) {
            this.setWay_size();
            if (ancestor != null)
                ancestor.sonIsSet();
        }
    }
    //
}

