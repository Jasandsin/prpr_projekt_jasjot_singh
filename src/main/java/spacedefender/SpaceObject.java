package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class SpaceObject {

    float x;
    float y;

    PApplet pApplet;
    PImage image;

    public void display() {
        pApplet.image(image, x, y);
    }

    public SpaceObject(PApplet pApplet, float x, float y){
        this.pApplet = pApplet;
        this.x = x;
        this.y = y;
    }

}
