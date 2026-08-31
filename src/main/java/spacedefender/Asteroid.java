package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Asteroid extends SpaceObject {

    float speed = 2;
    PImage spriteSheetAsteroid = pApplet.loadImage("UI-Design/Asteroids-0001.png");

    public Asteroid( PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.image = spriteSheetAsteroid.get(96, 16, 16, 16);
    }

    // Bewegt den Gegner nach unten
    public void move() {
        y = y + speed;
    }

    // Prüft, ob der Gegner den unteren Bildschirmrand verlassen hat
    public boolean isOffScreen() {
        return y > pApplet.height;
    }

    // Prüft, ob der Gegner das Raumschiff berührt
    public boolean hitsPlayer(float playerX, float playerY) {
        float distanceX = x - playerX;
        float distanceY = y - playerY;

        return Math.abs(distanceX) < 45 && Math.abs(distanceY) < 35;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBulletHitbox() {
        return 20;
    }
}
