package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Asteroid {

    float x;
    float y;
    float speed = 2;

    PApplet pApplet;
    PImage image;

    public Asteroid( PApplet pApplet, float x, float y) {
        this.pApplet = pApplet;
        this.x = x;
        this.y = y;
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

    // Zeichnet den Gegner
    public void display() {
        pApplet.image(image, x, y);
    }

    // Prüft, ob der Gegner das Raumschiff berührt
    public boolean hitsPlayer(float playerX, float playerY) {
        float distanceX = x - playerX;
        float distanceY = y - playerY;

        return Math.abs(distanceX) < 30 && Math.abs(distanceY) < 30;
    }

}
