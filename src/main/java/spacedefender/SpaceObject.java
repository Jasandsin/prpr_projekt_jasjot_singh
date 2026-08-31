package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class SpaceObject {

    float x;
    float y;
    float objectWidth;
    float objectHeight;

    PApplet pApplet;
    PImage image;

    public void display() {
        pApplet.image(image, x, y, objectWidth, objectHeight);
    }

    public SpaceObject(PApplet pApplet, float x, float y, float objectWidth, float objectHeight){
        this.pApplet = pApplet;
        this.x = x;
        this.y = y;
        this.objectWidth = objectWidth;
        this.objectHeight = objectHeight;
    }

    // Prüft, ob der SpaceObject das Raumschiff trifft
    public boolean hitsPlayer(Spaceship spaceship) {
        float distanceX = x - spaceship.x;
        float distanceY = y - spaceship.y;
        float hitboxX = (objectWidth / 2) + (spaceship.objectWidth / 2);
        float hitboxY = (objectHeight / 2) + (spaceship.objectHeight / 2);
        return Math.abs(distanceX) < hitboxX && Math.abs(distanceY) < hitboxY;
    }

}
