package spacedefender;

import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.SoundFile;

import java.util.ArrayList;
import java.util.Comparator;

import static spacedefender.FileLoader.getSoundFile;

public class SpaceDefender extends PApplet {

    public static void main(String[] args) {
        PApplet.main(SpaceDefender.class);
    }

    Spaceship spaceship;

    PImage backgroundImage;

    int score = 0;
    int lives = 3;
    int level = 1;
    String name = "";

    //Screens
    boolean gameOver = false;
    boolean gameStartScreen = true;
    boolean tutorialScreen = false;
    boolean highscoreScreen = false;
    boolean nameEntryScreen = false;

    boolean shooting = false;
    int lastShotTime = 0;
    int shootCooldown = 200;
    boolean bossSpawned = false;

    // Zeitpunkt des letzten Gegner-Spawns und Wartezeit zwischen zwei Gegnern
    int lastEnemySpawn = 0;
    int enemySpawnCooldown = 1000;

    //Liste mit allen Kugeln
    ArrayList<Bullet> bullets = new ArrayList<>();

    // Liste mit allen Gegnern
    ArrayList<Asteroid> asteroids = new ArrayList<>();

    ArrayList<Enemy> enemies = new ArrayList<>();

    ArrayList<HighscoreEntry> highscoreEntries = new ArrayList<>();


    @Override
    public void settings() {
        size(800, 600);
    }

    @Override
    public void setup() {
      //  backgroundImage = loadImage("PNG/blue-preview.png");
        //just to have an example for sound
        loadHighscores();
        SoundFile sound = getSoundFile(this, "main.ogg");
        sound.play();
        spaceship = new Spaceship(this, width / 2, height - 70);
        asteroids.add(new Asteroid(this,400, 50));
        enemies.add(new Enemy(this, 400, 50));
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

        if (nameEntryScreen) {
            fill(255);
            textAlign(CENTER);
            textSize(50);
            text("Gib deinen Namen für die Föderation", width / 2, 150);
            textSize(25);
            text("name: " + name, width / 2, 310);
            textSize(25);
            text("Press Enter to start", width / 2, 370);
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
            text("Highest Score", width / 2, 210);

            for(int i = 0; i < highscoreEntries.size(); i++){
                HighscoreEntry highscoreEntry = highscoreEntries.get(i);
                String highscoreText = (i+1) + " : " + highscoreEntry.getPlayerName() + " - " + highscoreEntry.getHighscoreOfPlayer();
                textSize(18);
                text(highscoreText, width / 2, 250 + (i*30));
            }
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

            textSize(25);
            text("highscore: " + highscoreEntries.getFirst().getHighscoreOfPlayer(), width / 2, 350);

            textSize(20);
            text("Press R to Replay", width / 2, 400);


            return;
        }


        // shooting 200ms abstand. 1000 ms / 200 ms = 5 Schüsse pro Sekunde
        // Solange die Leertaste gedrückt ist, wird nach jedem Cooldown eine neue Kugel erstellt
        if (shooting && millis() - lastShotTime >= shootCooldown) {
            bullets.add(new Bullet(this, spaceship.x, spaceship.y - 50, -1));
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




        // Prüft, ob eine Kugel einen Enemy oder Asteroid trifft
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            boolean bulletHit = false;
            if(bullet.isPlayerBullet()){
                for (int j = enemies.size() - 1; j >= 0; j--) {
                    Enemy enemy = enemies.get(j);
                    if (enemy instanceof EnemyBoss) {
                        if (bullet.hitsEnemy(enemy)) {
                            ((EnemyBoss) enemy).takeDamage();
                            bullets.remove(i);
                            bulletHit = true;
                            if (((EnemyBoss) enemy).isDead()) {
                                enemies.remove(j);
                                score = score + 1000;
                            }
                            break;
                        }
                    } else {
                        if (bullet.hitsEnemy(enemy)) {
                            bullets.remove(i);
                            enemies.remove(j);
                            score = score + 30;
                            bulletHit = true;
                            break;
                        }
                    }
                }
                        // Wenn bereits Enemy getroffen wurde,
                        // nächste Bullet prüfen
                        if (bulletHit) {
                            continue;
                        }


                    for (int j = asteroids.size() - 1; j >= 0; j--) {
                        Asteroid asteroid = asteroids.get(j);

                        if (bullet.hitsAsteroid(asteroid)) {
                            bullets.remove(i);
                            asteroids.remove(j);
                            score = score + 10;
                            break;
                        }
                    }
                }
                if (!bullet.isPlayerBullet() && bullet.hitsPlayer(spaceship)) {
                    bullets.remove(i);
                    lives = lives -1;
                    continue;
                }
                    }


        if (level == 3 && !bossSpawned) {
            enemies.add(new EnemyBoss(this, width / 2, 50));
            bossSpawned = true;
        }

            if(score >= 200){
                level = 3;
            }else if(score >= 100){
                level = 2;
            }else {
                level = 1;
            }


        // Bewegt und zeichnet alle Enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            switch (level) {
            case 3 -> {enemy.setSpeed(3);}
            case 2 -> {enemy.setSpeed(2);}
            default -> {enemy.setSpeed(1);}
            }
            enemy.move();
            enemy.display();
            if(enemy.canShoot()){
                bullets.add(new Bullet(this, enemy.x, enemy.y + 50, 1));
            }
            // Gegner trifft den Spieler
            if (enemy.hitsPlayer(spaceship.x, spaceship.y)){
                enemies.remove(i);
                lives = lives - 1;
                // Wenn keine Leben mehr vorhanden sind, ist das Spiel beendet
                if (lives <= 0) {
                    boolean playerExist = false;
                    HighscoreEntry highscore = new HighscoreEntry(name, score);
                    for (int h = 0; h < highscoreEntries.size(); h++) {
                        HighscoreEntry highscoreEntry = highscoreEntries.get(h);
                        if (highscoreEntry.getPlayerName().equals(name)){
                            playerExist = true;
                            if(highscoreEntry.getHighscoreOfPlayer() < score){
                                highscoreEntry.setHighscoreOfPlayer(score);
                            }
                        }
                    }
                    if(!playerExist){
                        highscoreEntries.add(highscore);
                    }
                    sortHighscores();
                    saveHighscores();
                    gameOver = true;
                }
                // continue = Dieser Durchlauf ist fertig. Geh zum nächsten Gegner.
                continue;
            }

            // Enemy hat den Bildschirm verlassen
            if (enemy.isOffScreen()) {
                enemies.remove(i);
            }
        }


        // Bewegt und zeichnet alle Asteroids
        for (int i = asteroids.size() - 1; i >= 0; i--) {
                Asteroid asteroid = asteroids.get(i);
            switch (level) {
            case 3 -> {asteroid.setSpeed(6);}
            case 2 -> {asteroid.setSpeed(4);}
            default -> {asteroid.setSpeed(2);}
            }
            asteroid.move();
            asteroid.display();
            // Asteroid trifft den Spieler
            if (asteroid.hitsPlayer(spaceship.x, spaceship.y)) {
                asteroids.remove(i);
                lives = lives - 1;
                // Wenn keine Leben mehr vorhanden sind, ist das Spiel beendet
                if (lives <= 0) {
                    boolean playerExist = false;
                    HighscoreEntry highscore = new HighscoreEntry(name, score);
                    for (int h = 0; h < highscoreEntries.size(); h++) {
                        HighscoreEntry highscoreEntry = highscoreEntries.get(h);
                        if (highscoreEntry.getPlayerName().equals(name)){
                            playerExist = true;
                            if(highscoreEntry.getHighscoreOfPlayer() < score){
                                highscoreEntry.setHighscoreOfPlayer(score);
                            }
                        }
                    }
                    if(!playerExist){
                        highscoreEntries.add(highscore);
                    }
                    sortHighscores();
                    saveHighscores();
                    gameOver = true;
                }
                // continue = Dieser Durchlauf ist fertig. Geh zum nächsten Gegner.
                continue;
            }

            // Asteriod hat den Bildschirm verlassen
            if (asteroid.isOffScreen()) {
                asteroids.remove(i);
            }
        }



        // Erstellt jede Sekunde einen neuen Asteroiden an einer zufälligen X-Position
        switch (level) {
        case 3 -> {enemySpawnCooldown = 250;}
        case 2 -> {enemySpawnCooldown = 500;}
        default -> {enemySpawnCooldown = 1000;}
        }
        if (millis() - lastEnemySpawn >= enemySpawnCooldown) {
            float enemyX = random(20, width - 20);
            switch (level) {
            case 3, 2 -> {
                int randomeAsteroid = (int) random(4);
                if(randomeAsteroid == 0){
                    asteroids.add(new Asteroid(this, enemyX, 0));
                }else if (randomeAsteroid == 1){
                    asteroids.add(new MediumAsteroid(this, enemyX, 0));
                } else if (randomeAsteroid == 3){
                    asteroids.add(new BigAsteroid(this, enemyX, 0));
                } else {
                    enemies.add(new Enemy(this, enemyX, 0));
                }
            }
            default -> {
                int randomeAsteroid = (int) random(2);
                if(randomeAsteroid == 0){
                    asteroids.add(new Asteroid(this, enemyX, 0));
                }else {
                    asteroids.add(new MediumAsteroid(this, enemyX, 0));
                }            }
            }
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
        text("Level: " + level, 20, 80);
    }

    @Override
    public void keyPressed() {

        // Startet das Spiel
        if (gameStartScreen && (key == 's' || key == 'S')) {
            gameStartScreen = false;
            nameEntryScreen = true;
            return;
        }

        if(nameEntryScreen){
           if(key == ENTER){
               if(!name.isEmpty()){
                   nameEntryScreen = false;
               }
           } else if(key == BACKSPACE) {
               if(!name.isEmpty()){
                   name = name.substring(0, name.length()-1);
               }
           }else {
               name = name + key;
           }
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
        level = 1;

        spaceship = new Spaceship(this, width /2, height-70);

        bullets.clear();
        asteroids.clear();
        enemies.clear();

        shooting = false;
        spaceship.moveLeft = false;
        spaceship.moveRight = false;

        lastEnemySpawn = millis();
    }

    public void sortHighscores(){
        highscoreEntries.sort(
            Comparator.comparingInt(HighscoreEntry::getHighscoreOfPlayer).reversed()
        );
    }

    public void saveHighscores(){
        String[] data = new String[highscoreEntries.size()];

        for(int i = 0; i<highscoreEntries.size(); i++){
            HighscoreEntry highscoreEntry = highscoreEntries.get(i);
            data[i] = highscoreEntry.getPlayerName() + ";" + highscoreEntry.getHighscoreOfPlayer();
        }

        saveStrings("src/main/resources/data/highscores.csv", data);
    }

    public void loadHighscores(){
        String[] data = loadStrings("src/main/resources/data/highscores.csv");
        if (data == null) {return;}
        for(int i = 0; i < data.length; i++){
            String[] parts = data[i].split(";");
            String playerName = parts[0];
            int playerScore = Integer.parseInt(parts[1]);
            HighscoreEntry highscoreEntry = new HighscoreEntry(playerName, playerScore);
            highscoreEntries.add(highscoreEntry);
        }
        sortHighscores();
    }

}