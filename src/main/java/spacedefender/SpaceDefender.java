package spacedefender;

import processing.core.PApplet;

import java.util.ArrayList;

public class SpaceDefender extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SpaceDefender.class);
    }

    // X und Y Position des Raumschiffes in diesen Variabeln speichern
    float playerX;
    float playerY;
    float playerSpeed = 5;
    int score = 0;
    int lives = 3;

    boolean gameOver = false;
    boolean gameStartScreen = true;
    boolean moveLeft = false;
    boolean moveRight = false;

    boolean shooting = false;
    int lastShotTime = 0;
    int shootCooldown = 200;

    // Zeitpunkt des letzten Gegner-Spawns und Wartezeit zwischen zwei Gegnern
    int lastEnemySpawn = 0;
    int enemySpawnCooldown = 1000;

    //Liste mit allen Kugeln
    ArrayList<Bullet> bullets = new ArrayList<>();

    // Liste mit allen Gegnern
    ArrayList<Enemy> enemies = new ArrayList<>();

    @Override
    public void settings() {
        size(800, 600);
    }

    @Override
    public void setup() {
        playerX = width / 2;
        playerY = height - 70;

        enemies.add(new Enemy(400, 50));

    }

    @Override
    public void draw() {
        background(20);

        // Zeigt den Start Screen mit Start, Tutorial und Highscore
        if (gameStartScreen) {
            fill(255);
            textAlign(CENTER);
            textSize(50);
            text("Welcome to SpaceDefender ", width / 2, 250);
            textSize(25);
            text("Start Game Press: S ", width / 2, 310);
            textSize(25);
            text("Start Tutorial Press: T ", width / 2, 370);
            textSize(25);
            text("See Highscore Press: H ", width / 2, 430);

            return;
        }

        // Zeigt den Game-Over-Screen und stoppt den restlichen Spielablauf
        if (gameOver) {
            fill(255);
            textAlign(CENTER);
            textSize(50);
            text("GAME OVER", width / 2, 250);

            textSize(25);
            text("Score: " + score, width / 2, 310);

            textSize(20);
            text("Press R to Replay", width / 2, 370);

            return;
        }

        if (moveLeft) {
            playerX = playerX - playerSpeed;
        }

        if (moveRight) {
            playerX = playerX + playerSpeed;
        }

        // max bis 20px links
        if (playerX < 20) {
            playerX = 20;
        }

        // Maximal bis 780px rechts
        if (playerX > width - 20) {
            playerX = width - 20;
        }

        // shooting 200ms abstand. 1000 ms / 200 ms = 5 Schüsse pro Sekunde
        // Solange die Leertaste gedrückt ist, wird nach jedem Cooldown eine neue Kugel erstellt
        if (shooting && millis() - lastShotTime >= shootCooldown) {
            bullets.add(new Bullet(playerX, playerY - 25));
            lastShotTime = millis();
        }

        // Bewegt und zeichnet alle Kugeln.
        // Kugeln ausserhalb des Bildschirms werden aus der Liste entfernt.
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.move();
            bullet.display(this);
            if (bullet.isOffScreen()) {
                bullets.remove(i);
            }
        }

        // Bewegt und zeichnet alle Gegner
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);

            enemy.move();
            enemy.display(this);

            // Gegner trifft den Spieler
            if (enemy.hitsPlayer(playerX, playerY)) {
                enemies.remove(i);
                lives = lives - 1;
                // Wenn keine Leben mehr vorhanden sind, ist das Spiel beendet
                if (lives <= 0) {
                    gameOver = true;
                }
                // continue = Dieser Durchlauf ist fertig. Geh zum nächsten Gegner.
                continue;
            }

            // Gegner hat den Bildschirm verlassen
            if (enemy.isOffScreen(this)) {
                enemies.remove(i);
            }
        }

        // Prüft, ob eine Kugel einen Gegner trifft
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);

            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);

                if (bullet.hits(enemy)) {
                    bullets.remove(i);
                    enemies.remove(j);
                    score = score + 10;
                    break;
                }
            }
        }

        // Erstellt jede Sekunde einen neuen Gegner an einer zufälligen X-Position
        if (millis() - lastEnemySpawn >= enemySpawnCooldown) {
            float enemyX = random(20, width - 20);
            enemies.add(new Enemy(enemyX, 0));
            lastEnemySpawn = millis();
        }


        //Raumschiff
            fill(0, 0, 255);
            triangle(
                playerX, playerY - 25,
                playerX - 20, playerY + 20,
                playerX + 20, playerY + 20
            );

        // Zeigt den aktuellen Punktestand oben links an
        textAlign(LEFT);
        fill(255);
        textSize(20);
        text("Score: " + score, 20, 30);
        text("Lives: " + lives, 20, 55);
    }

    @Override
    public void keyPressed() {

        // Startet das Spiel
        if (gameStartScreen && (key == 's' || key == 'S')) {
            gameStartScreen = false;
            return;
        }

        // Startet das Spiel nach Game Over neu
        if (gameOver && (key == 'r' || key == 'R')) {
            restartGame();
            return;
        }

        //Taste gedrückt Raumschiff bewegt sich nach links
        if (key == 'a' || key == 'A') {
            moveLeft = true;
        }

        //Taste gedrückt bewegt sich nach rechts
        if (key == 'd' || key == 'D') {
            moveRight = true;
        }

        // Nach leertaste Kugel erstellen bei playerX starten bei der spitze playerY-25
        // und speichern in bullets

        if (key == ' ') {
            shooting = true;
        }
    }

    @Override
    public void keyReleased() {

        //Taste losgelassen Bewegung links beendet
        if (key == 'a' || key == 'A') {
            moveLeft = false;
        }

        //Taste losgelassen bewegung rechts beendet
        if (key == 'd' || key == 'D') {
            moveRight = false;
        }

        if (key == ' ') {
            shooting = false;
        }

    }

    // Setzt alle Spielwerte zurück und startet ein neues Spiel
    public void restartGame() {
        score = 0;
        lives = 3;
        gameOver = false;

        playerX = width / 2;
        playerY = height - 70;

        bullets.clear();
        enemies.clear();

        shooting = false;
        moveLeft = false;
        moveRight = false;

        lastEnemySpawn = millis();
    }

}