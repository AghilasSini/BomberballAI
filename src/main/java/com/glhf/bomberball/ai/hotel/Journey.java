package com.glhf.bomberball.ai.hotel;

import com.glhf.bomberball.maze.cell.Cell;

public class Journey {
    private int nbmoves;
    private Cell destinationcell;

    public Journey(int nbmoves, Cell destinationcell) {
        this.nbmoves = nbmoves;
        this.destinationcell = destinationcell;
    }

    public int getNbmoves() {
        return nbmoves;
    }

    public void setNbmoves(int nbmoves) {
        this.nbmoves = nbmoves;
    }

    public Cell getDestinationcell() {
        return destinationcell;
    }

    public void setDestinationcell(Cell destinationcell) {
        this.destinationcell = destinationcell;
    }
}
