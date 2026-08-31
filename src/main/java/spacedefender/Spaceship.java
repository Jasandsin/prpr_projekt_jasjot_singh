package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Spaceship extends SpaceObject{

    float speed = 5;

    boolean moveLeft = false;
    boolean moveRight = false;

    public Spaceship( PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        PImage spriteSheet = pApplet.loadImage("UI-Design/SpaceShips_Player-0001.png");
        this.image = spriteSheet.get(140, 22, 39, 41);
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
