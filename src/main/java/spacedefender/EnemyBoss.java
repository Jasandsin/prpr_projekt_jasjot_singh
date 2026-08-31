package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class EnemyBoss extends Enemy{

    int health = 150;
    PImage spriteSheetBoss = pApplet.loadImage("UI-Design/SpaceShip_Boss-0001.png");

    public EnemyBoss(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.shootCooldown = 1000;
        image = spriteSheetBoss.get(139, 36, 106, 106);
        objectWidth = 120;
        objectHeight = 120;
    }

    public int getHealth() {
        return health;
    }

    void takeDamage(){
        health = health - 1;
    }

    boolean isDead(){
        if(health <= 0){
            return true;
        }
        return false;
    }

    @Override
    public void move(){
        y = objectHeight / 2 + 20;
        x = x + (speed * directionX);

        // Wenn am Rand
        if (x >= pApplet.width - objectWidth / 2) {
            directionX = -1;
        }
        if (x <= objectWidth / 2) {
            directionX = 1;
        }
    }

}
