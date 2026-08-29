package spacedefender;

import processing.core.PApplet;

public class Asteroid extends SpaceObject {

    float speed = 2;

    public Asteroid( PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.image = pApplet.loadImage("PNG/Meteors/meteorGrey_med1.png");
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

        return Math.abs(distanceX) < 30 && Math.abs(distanceY) < 30;
    }

}
