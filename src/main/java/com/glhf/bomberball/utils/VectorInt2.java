package com.glhf.bomberball.utils;

public class VectorInt2 {
    public int x;
    public int y;

    public VectorInt2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public VectorInt2(VectorInt2 v) {
        this.x=v.x;
        this.y=v.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double dst(int x, int y) {
        final int x_d = x - this.x;
        final int y_d = y - this.y;
        return Math.sqrt(x_d * x_d + y_d * y_d);
    }

    public VectorInt2 sub (VectorInt2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public VectorInt2 scl (float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public VectorInt2 mulAdd (VectorInt2 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }

    public boolean equals(int x, int y) {
        return this.x==x && this.y==y;
    }

    public VectorInt2 abs() {
        x=Math.abs(x);
        y=Math.abs(y);
        return this;
    }
}
