package com.glhf.bomberball.gameobject;

import java.util.Observable;

public class Score extends Observable {
    int score;
    private Score(){
        score=0;
    }
    private static Score INSTANCE = new Score();
    public static Score getINSTANCE(){
        return INSTANCE;
    }
    public void increaseScore(int n){
        this.score+=n;
        System.out.println("New score = " + score);
        setChanged();
        notifyObservers();
    }
    public void resetScore(){
        this.score=0;
        System.out.println("Reset of the score");
        setChanged();
        notifyObservers();
    }
    public int getScore(){
        return score;
    }
}
