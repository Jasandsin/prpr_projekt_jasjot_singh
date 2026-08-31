package spacedefender;

import processing.core.PApplet;

public class MediumAsteroid extends Asteroid{

    public MediumAsteroid(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.image = spriteSheetAsteroid.get(83, 35, 26, 26);
        objectHeight = 52;
        objectWidth = 52;
    }

    }
