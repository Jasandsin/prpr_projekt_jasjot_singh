package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;

public class EnemyBoss extends Enemy{

    int health = 5;
    PImage spriteSheetBoss = pApplet.loadImage("UI-Design/SpaceShip_Boss-0001.png");



    public EnemyBoss(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.shootCooldown = 1500;
        image = spriteSheetBoss.get(139, 36, 106, 106);
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
        y = 50;
        x = x + (speed * directionX);

        // Wenn am Rand
        if (x >= pApplet.width - 20) {
            directionX = -1;
        }
        if (x <= 20) {
            directionX = 1;
        }
    }

}
