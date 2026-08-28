package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Spaceship {

    // X und Y Position des Raumschiffes in diesen Variabeln speichern
    float x;
    float y;
    float speed = 5;

    boolean moveLeft = false;
    boolean moveRight = false;

    protected PApplet pApplet;
    PImage image;

    public Spaceship( PApplet pApplet, float x, float y) {
        this.pApplet = pApplet;
        this.x = x;
        this.y = y;
        this.image = pApplet.loadImage("PNG/playerShip1_blue.png");
    }

    public void move(){
        if (moveLeft) {
            x = x - speed;
        }
        if (moveRight) {
            x = x + speed;
        }
        // max bis 20px links
        if (x < 20) {
            x = 20;
        }
        // Maximal bis 780px rechts
        if (x > pApplet.width - 20) {
            x = pApplet.width - 20;
        }

    }

    public void display(){
        pApplet.image(image, x, y);
    }

}
