package com.glhf.bomberball.utils;

public enum Directions {
    RIGHT,
    UP,
    LEFT,
    DOWN;

    public Directions opposite() {
        return values()[(ordinal()+2)%4];
    }
}
