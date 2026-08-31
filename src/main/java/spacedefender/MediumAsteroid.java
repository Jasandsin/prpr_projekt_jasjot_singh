package spacedefender;

import processing.core.PApplet;

public class MediumAsteroid extends Asteroid{

    public MediumAsteroid(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.image = spriteSheetAsteroid.get(83, 35, 26, 26);
    }

    @Override
        public boolean hitsPlayer(float playerX, float playerY) {
            float distanceX = x - playerX;
            float distanceY = y - playerY;

        return Math.abs(distanceX) < 55 && Math.abs(distanceY) < 45;
        }

    @Override
    public float getBulletHitbox() {
        return 30;
    }
    }
