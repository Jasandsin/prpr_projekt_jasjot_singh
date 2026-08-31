package spacedefender;

import processing.core.PApplet;

public class BigAsteroid extends Asteroid{

    public BigAsteroid(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.image = spriteSheetAsteroid.get(70, 69, 39, 38);
    }

    @Override
    public boolean hitsPlayer(float playerX, float playerY) {
        float distanceX = x - playerX;
        float distanceY = y - playerY;

        return Math.abs(distanceX) < 80 && Math.abs(distanceY) < 60;
    }

    @Override
    public float getBulletHitbox() {
        return 50;
    }
}
