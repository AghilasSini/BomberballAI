package com.glhf.bomberball.gameobject;

import com.glhf.bomberball.config.GameInfiniteConfig;

import java.util.Observable;

public class NumberTurn extends Observable {
    int nbturn;
    private NumberTurn(){
        nbturn=GameInfiniteConfig.get().nbturnmax;
    }
    private static NumberTurn INSTANCE = new NumberTurn();
    public static NumberTurn getINSTANCE(){
        return INSTANCE;
    }
    public void decreaseTurn(int n){
        this.nbturn-=n;
        System.out.println("nb turn left = " + nbturn);
        setChanged();
        notifyObservers();
    }
    public void resetNbTurn(){
        this.nbturn=GameInfiniteConfig.get().nbturnmax;
        System.out.println("Reset of nb turn left");
        setChanged();
        notifyObservers();
    }
    public int getNbTurn(){
        return nbturn;
    }
}
