package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Bullet  {

    //Kugel position
    float x;
    float y;

    float speed = 8;

    PImage image;
    PApplet pApplet;

    public Bullet(PApplet pApplet, float x, float y) {
        this.pApplet = pApplet;
        this.x = x;
        this.y = y;
        this.image = pApplet.loadImage("PNG/Lasers/laserBlue01.png");
    }

    //Schiesst nach oben y = 0 ist oben deshalb y - speed (verkleinern)
    public void move() {
        y = y - speed;
    }

    // Prüft, ob die Kugel den oberen Bildschirmrand verlassen hat
    public boolean isOffScreen() {
        return y < 0;
    }

    public void display() {
        pApplet.image(image, x, y);
    }

    // Prüft, ob die Kugel einen Gegner getroffen hat
    public boolean hits(Asteroid asteroid) {
        float distanceX = x - asteroid.x;
        float distanceY = y - asteroid.y;

        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < 20 && Math.abs(distanceY) < 20;
    }

}
