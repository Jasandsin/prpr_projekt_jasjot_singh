package spacedefender;

import processing.core.PApplet;
import processing.sound.SoundFile;

import static spacedefender.FileLoader.getSoundFile;

public class Enemy extends SpaceObject{

    int speed = 2;
    int directionX = 1;
    float startX;
    int lastShotTime = 0;
    int shootCooldown = 2000;

    public Enemy(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.startX = x;
        this.image = pApplet.loadImage("PNG/Enemies/enemyBlack1.png");
    }

    public void move(){
        y = y + speed;
        x = x + (speed * directionX);

        // Wenn am Rand
        if (x >= pApplet.width - 20) {
            directionX = -1;
        }
        if (x <= 20) {
            directionX = 1;
        }

        // 50 Pixel bewegen beim spawn punkt
        if (x >= startX + 50) {
            directionX = -1;
        }
        if (x <= startX - 50) {
            directionX = 1;
        }
    }

    // Prüft, ob der Gegner das Raumschiff berührt
    public boolean hitsPlayer(float playerX, float playerY) {
        float distanceX = x - playerX;
        float distanceY = y - playerY;

        return Math.abs(distanceX) < 45 && Math.abs(distanceY) < 35;
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
