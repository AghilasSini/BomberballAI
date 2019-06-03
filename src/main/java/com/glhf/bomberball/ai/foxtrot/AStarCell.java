package com.glhf.bomberball.ai.foxtrot;

public class AStarCell implements Comparable {
    private int x;
    private int y;
    private int cout;
    private double heuristique;
    private AStarCell last;
    public AStarCell(int x,int y,int cout,double heuristique,AStarCell last) {
        this.x = x;
        this.y = y;
        this.cout = cout;
        this.heuristique = heuristique;
    }

    public double getHeuristique() {
        return heuristique;
    }

    public void setHeuristique(double heuristique) {
        this.heuristique = heuristique;
    }

    public int getCout() {
        return cout;
    }

    public void setCout(int cout) {
        this.cout = cout;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof AStarCell))
            return false;
        if (obj==this)
            return true;
        return getX() == ((AStarCell) obj).getX() && getY() == ((AStarCell) obj).getY();
    }

    @Override
    public int hashCode() {
        return x;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof AStarCell) {
            AStarCell c = (AStarCell) o;
            if (x==c.getX()&&y==c.getY()) {
                return 0;
            }
            else if (heuristique>c.getHeuristique()) {
                return 1;
            }
            else {
                return -1;
            }

        }
        return 0;
    }

    public AStarCell getLast() {
        return last;
    }

    public void setLast(AStarCell last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return x+"  "+y;
    }
}
