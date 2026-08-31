package spacedefender;

import processing.core.PApplet;

public class BigAsteroid extends Asteroid {

    public BigAsteroid(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.image = spriteSheetAsteroid.get(70, 69, 39, 38);
        objectWidth = 78;
        objectHeight = 76;
    }

}
