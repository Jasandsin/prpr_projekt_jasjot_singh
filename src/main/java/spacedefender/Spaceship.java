package spacedefender;

import processing.core.PApplet;

public class Spaceship extends SpaceObject{

    float speed = 5;

    boolean moveLeft = false;
    boolean moveRight = false;

    public Spaceship( PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
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

}
