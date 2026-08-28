package spacedefender;

import processing.core.PApplet;

public class Bullet  {

    //Kugel position
    float x;
    float y;

    float speed = 8;

    public Bullet(float x, float y) {
        this.x = x;
        this.y = y;
    }

    //Schiesst nach oben y = 0 ist oben deshalb y - speed (verkleinern)
    public void move() {
        y = y - speed;
    }

    // Prüft, ob die Kugel den oberen Bildschirmrand verlassen hat
    public boolean isOffScreen() {
        return y < 0;
    }

    public void display(PApplet app) {
        app.fill(255, 255, 0);
        app.rect(x - 2, y, 4, 12);
    }

    // Prüft, ob die Kugel einen Gegner getroffen hat
    public boolean hits(Asteroid asteroid) {
        float distanceX = x - asteroid.x;
        float distanceY = y - asteroid.y;

        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < 20 && Math.abs(distanceY) < 20;
    }

}
