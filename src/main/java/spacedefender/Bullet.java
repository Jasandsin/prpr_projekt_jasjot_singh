package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Bullet extends SpaceObject  {

    float speed = 8;
    int directionY;
    PImage spriteSheetBullet = pApplet.loadImage("UI-Design/Bullets-0001.png");


    public Bullet(PApplet pApplet, float x, float y, int directionY) {
        super(pApplet, x, y, 10, 27);
        this.directionY = directionY;
        if(isPlayerBullet()){
            image = spriteSheetBullet.get(148, 16, 7, 19);
        } else {
            image = spriteSheetBullet.get(84, 80, 8, 16);
        }
    }

    //Schiesst nach oben y = 0 ist oben deshalb y - speed (verkleinern)
    public void move() {
        y = y + (speed * directionY);
    }

    // Prüft, ob die Kugel den oberen Bildschirmrand verlassen hat
    public boolean isOffScreen() {
        return y < 0 || y > pApplet.height;
    }

    // Prüft, ob die Kugel ein Asteroid getroffen hat
    public boolean hitsAsteroid(Asteroid asteroid) {
        float distanceX = x - asteroid.x;
        float distanceY = y - asteroid.y;
        float hitboxX = (objectWidth / 2) + (asteroid.objectWidth / 2);
        float hitboxY = (objectHeight / 2) + (asteroid.objectHeight / 2);
        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < hitboxX && Math.abs(distanceY) < hitboxY;
    }

    // Prüft, ob die Kugel einen Gegner getroffen hat
    public boolean hitsEnemy(Enemy enemy) {
        float distanceX = x - enemy.x;
        float distanceY = y - enemy.y;
        float hitboxX = (objectWidth / 2) + (enemy.objectWidth / 2);
        float hitboxY = (objectHeight / 2) + (enemy.objectHeight / 2);
        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < hitboxX && Math.abs(distanceY) < hitboxY;
    }

    // Prüft, ob die Kugel einen Gegner getroffen hat
    public boolean hitsPlayer(Spaceship spaceship) {
        float distanceX = x - spaceship.x;
        float distanceY = y - spaceship.y;
        float hitboxX = (objectWidth / 2) + (spaceship.objectWidth / 2);
        float hitboxY = (objectHeight / 2) + (spaceship.objectHeight / 2);
        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < hitboxX && Math.abs(distanceY) < hitboxY;
    }

    public boolean isPlayerBullet(){
        if(directionY == -1){
            return true;
        }
        return false;
    }

}
