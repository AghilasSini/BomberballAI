package com.glhf.bomberball.utils;

public enum Resolutions {
    //VERY_LOW	(560,	315),  // 16:9
    LOW			(800,	450),
    MEDIUM		(1040,	585),
    HIGH		(1760,	990),
    VERY_HIGH	(2560,	1440);

    public final int width;
    public final int height;

    Resolutions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return width+"x"+height;
    }
}
