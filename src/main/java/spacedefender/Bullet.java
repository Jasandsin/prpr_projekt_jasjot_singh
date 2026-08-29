package spacedefender;

import processing.core.PApplet;

public class Bullet extends SpaceObject  {

    float speed = 8;

    public Bullet(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
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

    // Prüft, ob die Kugel einen Gegner getroffen hat
    public boolean hits(Asteroid asteroid) {
        float distanceX = x - asteroid.x;
        float distanceY = y - asteroid.y;

        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < 20 && Math.abs(distanceY) < 20;
    }

}
