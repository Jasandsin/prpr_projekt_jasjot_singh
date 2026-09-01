package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Explosion {

    PApplet pApplet;
    PImage[] frames;

    float x;
    float y;
    float scale;

    int currentFrame = 0;
    int lastFrameTime;
    int frameDuration = 70;

    public Explosion(PApplet pApplet, float x, float y, PImage[] frames, float scale) {
        this.pApplet = pApplet;
        this.x = x;
        this.y = y;
        this.frames = frames;
        this.scale = scale;
        this.lastFrameTime = pApplet.millis();
    }

    public void display() {
        if (!isFinished()) {
            PImage currentImage = frames[currentFrame];
            pApplet.image(currentImage, x, y, currentImage.width * scale, currentImage.height * scale);
        }
    }

    public void update() {
        if (pApplet.millis() - lastFrameTime >= frameDuration) {
            currentFrame++;
            lastFrameTime = pApplet.millis();
        }
    }

    public boolean isFinished() {
        return currentFrame >= frames.length;
    }
}
