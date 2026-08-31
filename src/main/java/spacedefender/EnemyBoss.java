package spacedefender;

import processing.core.PApplet;

public class EnemyBoss extends Enemy{

    int health = 5;


    public EnemyBoss(PApplet pApplet, float x, float y) {
        super(pApplet, x, y);
        this.image = pApplet.loadImage("PNG/Enemies/enemyRed5.png");
        this.shootCooldown = 1500;
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
