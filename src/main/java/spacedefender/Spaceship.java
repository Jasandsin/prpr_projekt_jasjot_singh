package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Spaceship extends SpaceObject{

    float speed = 8;

    boolean moveLeft = false;
    boolean moveRight = false;

    public Spaceship( PApplet pApplet, float x, float y) {
        super(pApplet, x, y, 60, 63);
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
        if (x < objectWidth / 2) {
            x = objectWidth / 2;
        }
        // Maximal bis 780px rechts
        if (x > pApplet.width - objectWidth / 2) {
            x = pApplet.width - objectWidth / 2;
        }
    }

}
