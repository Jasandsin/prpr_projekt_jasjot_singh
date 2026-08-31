package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Enemy extends SpaceObject{

    int speed = 1;
    int directionX = 1;
    float startX;
    int lastShotTime = 0;
    int shootCooldown = 2000;
    PImage spriteSheet = pApplet.loadImage("UI-Design/SpaceShips_Enemy-0001.png");


    public Enemy(PApplet pApplet, float x, float y) {
        super(pApplet, x, y, 68, 54);
        this.startX = x;
        this.image = spriteSheet.get(92, 17, 45, 36);
    }

    public void move(){
        y = y + speed;
        x = x + (speed * directionX);

        // Wenn am Rand
        if (x >= pApplet.width - objectWidth / 2) {
            directionX = -1;
        }
        if (x <= objectWidth / 2) {
            directionX = 1;
        }

        // 50 Pixel bewegen beim spawn punkt
        if (x >= startX + 80) {
            directionX = -1;
        }
        if (x <= startX - 80) {
            directionX = 1;
        }
    }

    public boolean canShoot(){
        if (pApplet.millis() - lastShotTime >= shootCooldown) {
            lastShotTime = pApplet.millis();
            return true;
        }
        return false;
    }

    public boolean isOffScreen() {
        return y > pApplet.height;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setDirectionX(int directionX) {
        this.directionX = directionX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }
}
