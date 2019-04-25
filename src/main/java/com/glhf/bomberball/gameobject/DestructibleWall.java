package com.glhf.bomberball.gameobject;

import com.glhf.bomberball.Graphics;

public class DestructibleWall extends Wall {

    /**
     * Constructor for the DestructibleWall class
     */
    public DestructibleWall()
    {
        super();
        this.sprite = Graphics.Sprites.get("crate");
    }

    @Override
    public GameObject clone() {
        DestructibleWall o = new DestructibleWall();
        o.sprite = this.sprite;
        return o;
    }
}
