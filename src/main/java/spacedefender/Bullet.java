package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class Bullet extends SpaceObject  {

    float speed = 8;
    int directionY;
    PImage spriteSheetBullet = pApplet.loadImage("UI-Design/Bullets-0001.png");


    public Bullet(PApplet pApplet, float x, float y, int directionY) {
        super(pApplet, x, y);
        this.directionY = directionY;
        image = spriteSheetBullet.get(148, 16, 7, 19);
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
        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < asteroid.getBulletHitbox() && Math.abs(distanceY) < asteroid.getBulletHitbox();
    }

    // Prüft, ob die Kugel einen Gegner getroffen hat
    public boolean hitsEnemy(Enemy enemy) {
        float distanceX = x - enemy.x;
        float distanceY = y - enemy.y;
        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < 20 && Math.abs(distanceY) < 20;
    }

    // Prüft, ob die Kugel einen Gegner getroffen hat
    public boolean hitsPlayer(Spaceship spaceship) {
        float distanceX = x - spaceship.x;
        float distanceY = y - spaceship.y;
        //Math.abs negative zahlen positiv bullet kann r or l sein vom mittelpunkt des enemies
        return Math.abs(distanceX) < 20 && Math.abs(distanceY) < 20;
    }

    public boolean isPlayerBullet(){
        if(directionY == -1){
            return true;
        }
        return false;
    }

}
