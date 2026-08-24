package spacedefender;

import processing.core.PApplet;

public class Enemy {


    float x;
    float y;
    float speed = 2;

    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Bewegt den Gegner nach unten
    public void move() {
        y = y + speed;
    }

    // Prüft, ob der Gegner den unteren Bildschirmrand verlassen hat
    public boolean isOffScreen(PApplet app) {
        return y > app.height;
    }

    // Zeichnet den Gegner
    public void display(PApplet app) {
        app.fill(255, 0, 0);
        app.ellipse(x, y, 40, 40);
    }

    // Prüft, ob der Gegner das Raumschiff berührt
    public boolean hitsPlayer(float playerX, float playerY) {
        float distanceX = x - playerX;
        float distanceY = y - playerY;

        return Math.abs(distanceX) < 30 && Math.abs(distanceY) < 30;
    }

}
