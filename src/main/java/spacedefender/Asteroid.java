package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Asteroid extends SpaceObject {

    float speed = 2;
    PImage spriteSheetAsteroid = pApplet.loadImage("UI-Design/Asteroids-0001.png");

    public Asteroid(PApplet pApplet, float x, float y) {
        super(pApplet, x, y, 32, 32);
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


    public void setSpeed(float speed) {
        this.speed = speed;
    }

}
