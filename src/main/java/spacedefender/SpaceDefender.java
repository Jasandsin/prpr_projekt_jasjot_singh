package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.SoundFile;

import java.util.ArrayList;

import static spacedefender.FileLoader.getSoundFile;

public class SpaceDefender extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SpaceDefender.class);
    }

    Spaceship spaceship;

    PImage backgroundImage;

    int score = 0;
    int highscore = 0;
    int lives = 3;

    //Screens
    boolean gameOver = false;
    boolean gameStartScreen = true;
    boolean tutorialScreen = false;
    boolean highscoreScreen = false;

    boolean shooting = false;
    int lastShotTime = 0;
    int shootCooldown = 200;

    // Zeitpunkt des letzten Gegner-Spawns und Wartezeit zwischen zwei Gegnern
    int lastEnemySpawn = 0;
    int enemySpawnCooldown = 1000;

    //Liste mit allen Kugeln
    ArrayList<Bullet> bullets = new ArrayList<>();

    // Liste mit allen Gegnern
    ArrayList<Asteroid> asteroids = new ArrayList<>();


    @Override
    public void settings() {
        size(800, 600);
    }

    @Override
    public void setup() {
      //  backgroundImage = loadImage("PNG/blue-preview.png");
        //just to have an example for sound
        SoundFile sound = getSoundFile(this, "main.ogg");
        sound.play();
        spaceship = new Spaceship(this, width / 2, height - 70);
        asteroids.add(new Asteroid(this,400, 50));
        imageMode(CENTER);
    }

    @Override
    public void draw() {
        background(20);

        // Zeigt den Start Screen mit Start, Tutorial und Highscore
        if (gameStartScreen) {
            fill(255);
            textAlign(CENTER);
            textSize(50);
            text("Welcome to SpaceDefender ", width / 2, 150);
            textSize(25);
            text("Start Game Press: S ", width / 2, 310);
            textSize(25);
            text("Start Tutorial Press: T ", width / 2, 370);
            textSize(25);
            text("See Highscore Press: H ", width / 2, 430);

            return;
        }

        //Tutorial Screen mit Controller logik und Spiel Anweisungen
        if (tutorialScreen) {
            fill(255);
            textAlign(CENTER);
            textSize(50);
            text("Tutorial ", width / 2, 150);
            textSize(18);
            text("A = Move Left ", width / 2, 210);
            textSize(18);
            text("D = Move Right ", width / 2, 240);
            textSize(18);
            text("Space = Shoot ", width / 2, 270);
            textSize(18);
            text("You are a Spacedefender.\n " +
                "Shoot the astroids and enemies.\n  " +
                "You have 3 lives if you loose them your space ship gets destroyed. ", width / 2, 330);
            textSize(15);
            text("Press B to go back to Start Screen ", width / 2, 500);
            return;
        }

        //Highscore Screen
        if (highscoreScreen) {
            fill(255);
            textAlign(CENTER);
            textSize(50);
            text("HighScore ", width / 2, 150);
            textSize(18);
            text("Highest Score: " + highscore, width / 2, 210);
            textSize(15);
            text("Press B to go back to Start Screen ", width / 2, 500);
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

            if(score > highscore){
                highscore = score;
            }

            textSize(25);
            text("highscore: " + highscore, width / 2, 350);

            textSize(20);
            text("Press R to Replay", width / 2, 400);


            return;
        }



        // shooting 200ms abstand. 1000 ms / 200 ms = 5 Schüsse pro Sekunde
        // Solange die Leertaste gedrückt ist, wird nach jedem Cooldown eine neue Kugel erstellt
        if (shooting && millis() - lastShotTime >= shootCooldown) {
            bullets.add(new Bullet(this, spaceship.x, spaceship.y - 50));
            lastShotTime = millis();
            SoundFile shootsound = getSoundFile(this, "enemy_shoot.ogg");
            shootsound.play();
        }

        // Bewegt und zeichnet alle Kugeln.
        // Kugeln ausserhalb des Bildschirms werden aus der Liste entfernt.
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.move();
            bullet.display();
            if (bullet.isOffScreen()) {
                bullets.remove(i);
            }
        }

        // Bewegt und zeichnet alle Gegner
        for (int i = asteroids.size() - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);

            asteroid.move();
            asteroid.display();

            // Gegner trifft den Spieler
            if (asteroid.hitsPlayer(spaceship.x, spaceship.y)) {
                asteroids.remove(i);
                lives = lives - 1;
                // Wenn keine Leben mehr vorhanden sind, ist das Spiel beendet
                if (lives <= 0) {
                    gameOver = true;
                }
                // continue = Dieser Durchlauf ist fertig. Geh zum nächsten Gegner.
                continue;
            }

            // Gegner hat den Bildschirm verlassen
            if (asteroid.isOffScreen()) {
                asteroids.remove(i);
            }
        }

        // Prüft, ob eine Kugel einen Gegner trifft
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);

            for (int j = asteroids.size() - 1; j >= 0; j--) {
                Asteroid asteroid = asteroids.get(j);

                if (bullet.hits(asteroid)) {
                    bullets.remove(i);
                    asteroids.remove(j);
                    score = score + 10;
                    break;
                }
            }
        }

        // Erstellt jede Sekunde einen neuen Gegner an einer zufälligen X-Position
        if (millis() - lastEnemySpawn >= enemySpawnCooldown) {
            float enemyX = random(20, width - 20);
            asteroids.add(new Asteroid(this, enemyX, 0));
            lastEnemySpawn = millis();
        }


        //Raumschiff
        spaceship.move();
        spaceship.display();

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

        // Startet Tutorial
        if (gameStartScreen && (key == 't' || key == 'T')) {
            tutorialScreen = true;
            gameStartScreen = false;
            return;
        }

        // Startet Highscore
        if (gameStartScreen && (key == 'h' || key == 'H')) {
            highscoreScreen = true;
            gameStartScreen = false;
            return;
        }

        // Back Key
        if ((tutorialScreen || highscoreScreen || gameStartScreen == false) && gameOver == false && (key == 'b' || key == 'B')) {
            highscoreScreen = false;
            tutorialScreen = false;
            gameStartScreen = true;
            restartGame();
            return;
        }


        if (gameOver && (key == 'r' || key == 'R')) {
            restartGame();
            return;
        }

        //Taste gedrückt Raumschiff bewegt sich nach links
        if (key == 'a' || key == 'A') {
            spaceship.moveLeft = true;
        }

        //Taste gedrückt bewegt sich nach rechts
        if (key == 'd' || key == 'D') {
            spaceship.moveRight = true;
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
            spaceship.moveLeft = false;
        }

        //Taste losgelassen bewegung rechts beendet
        if (key == 'd' || key == 'D') {
            spaceship.moveRight = false;
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

        spaceship = new Spaceship(this, width /2, height-70);


        bullets.clear();
        asteroids.clear();

        shooting = false;
        spaceship.moveLeft = false;
        spaceship.moveRight = false;

        lastEnemySpawn = millis();
    }

}