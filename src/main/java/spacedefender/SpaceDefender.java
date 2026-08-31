package spacedefender;

import processing.core.PApplet;
import processing.core.PFont;
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
    PImage uiSheet;
    PImage scorePanel;
    PImage heartIcon;
    PFont gameFont;

    SoundFile menuMusic;
    SoundFile gameMusic;
    SoundFile gameOverMusic;
    SoundFile VictoryMusic;

    int score = 0;
    int lives = 3;
    int level = 1;
    String name = "";
    int backgroundY = 0;

    //Screens
    boolean gameOverScreen = false;
    boolean gameStartScreen = true;
    boolean tutorialScreen = false;
    boolean highscoreScreen = false;
    boolean nameEntryScreen = false;
    boolean VictoryScreen = false;

    boolean shooting = false;
    int lastShotTime = 0;
    int shootCooldown = 200;
    boolean bossSpawned = false;

    // Zeitpunkt des letzten Gegner-Spawns und Wartezeit zwischen zwei Gegnern
    int lastEnemySpawn = 0;
    int enemySpawnCooldown = 1000;

    //Liste mit allen Kugeln
    ArrayList<Bullet> bullets = new ArrayList<>();

    // Liste mit allen Asteroids
    ArrayList<Asteroid> asteroids = new ArrayList<>();

    // Liste mit allen Gegnern
    ArrayList<Enemy> enemies = new ArrayList<>();

    // Liste mit allen highscores
    ArrayList<HighscoreEntry> highscoreEntries = new ArrayList<>();


    @Override
    public void settings() {
        size(800, 600);
    }

    @Override
    public void setup() {
        level = 3;
        menuMusic = getSoundFile(this, "Brave Pilots (Menu Screen).ogg");
        gameMusic = getSoundFile(this, "Battle in the Stars.ogg");
        gameOverMusic = getSoundFile(this, "Defeated (Game Over Tune).ogg");
        VictoryMusic = getSoundFile(this, "Victory Tune.ogg");
        menuMusic.loop();

        // Font
        gameFont = createFont("UI-Design/kenvector_future.ttf", 32);
        textFont(gameFont);
        //Background
        backgroundImage = loadImage("UI-Design/Background_Full-0001.png");
        // UI
        uiSheet = loadImage("UI-Design/UI_sprites-0001.png");
        scorePanel = uiSheet.get(4, 11, 73, 21);
        heartIcon = uiSheet.get(3, 82, 13, 11);
        //just to have an example for sound
        loadHighscores();
        spaceship = new Spaceship(this, width / 2, height - 70);
        imageMode(CENTER);
    }

    @Override
    public void draw() {

        // Erstellt Background und scrollt im Hintergrund
        image(backgroundImage, width / 2, height / 2 + backgroundY, width, height);
        image(backgroundImage, width / 2, backgroundY - height / 2 , width, height);
        backgroundY = backgroundY + 1;
        if(backgroundY >= height){
            backgroundY = 0;
        }

        // Zeigt den Start Screen mit Start, Tutorial und Highscore
        if (gameStartScreen) { drawStartScreen(); return; }
        if (nameEntryScreen) { drawNameEntryScreen(); return; }

        //Tutorial Screen mit Controller logik und Spiel Anweisungen
        if (tutorialScreen) { drawTutorialScreen(); return; }

        //Highscore Screen
        if (highscoreScreen) { drawHighscoreScreen(); return; }

        // Zeigt den Game-Over-Screen und stoppt den restlichen Spielablauf
        if (gameOverScreen) { drawGameOverScreen(); return; }

        if(VictoryScreen) {
            drawVictoryScreen();
            return;
        }

        // shooting 200ms abstand. 1000 ms / 200 ms = 5 Schüsse pro Sekunde
        // Solange die Leertaste gedrückt ist, wird nach jedem Cooldown eine neue Kugel erstellt
        if (shooting && millis() - lastShotTime >= shootCooldown) {
            bullets.add(new Bullet(this, spaceship.x, spaceship.y - 50, -1));
            lastShotTime = millis();
            SoundFile shootsound = getSoundFile(this, "player_shoot.ogg");
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
                                SoundFile destroySound = getSoundFile(this, "explosion-large_3.wav");
                                destroySound.play();
                                VictoryScreen = true;
                                startVictoryMusic();
                            }
                            break;
                        }
                    } else {
                        if (bullet.hitsEnemy(enemy)) {
                            bullets.remove(i);
                            enemies.remove(j);
                            score = score + 30;
                            bulletHit = true;
                            SoundFile destroySound = getSoundFile(this, "explosionCrunch_000.ogg");
                            destroySound.play();
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

                            SoundFile destroySound = getSoundFile(this, "explosionCrunch_004.ogg");
                            destroySound.play();

                            break;
                        }
                    }
                }
                if (!bullet.isPlayerBullet() && bullet.hitsPlayer(spaceship)) {
                    bullets.remove(i);
                    lives = lives -1;
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

                        SoundFile destroySound = getSoundFile(this, "player_explotion.ogg");
                        destroySound.play();

                        gameOverScreen = true;
                        startGameOverMusic();
                        return;
                    }
                }
        }


        if (level == 3 && !bossSpawned) {
            enemies.add(new EnemyBoss(this, width / 2, 50));
            bossSpawned = true;
            SoundFile enemyShootSound = getSoundFile(this, "engine-looping_2.wav");
            enemyShootSound.play();
        }
            if(score >= 500){
                level = 3;
            }else if(score >= 300){
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
                SoundFile enemyShootSound = getSoundFile(this, "enemy_shoot.ogg");
                enemyShootSound.play();
            }
            // Gegner trifft den Spieler
            if (enemy.hitsPlayer(spaceship)){
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
                    SoundFile destroySound = getSoundFile(this, "player_explotion.ogg");
                    destroySound.play();
                    gameOverScreen = true;
                    startGameOverMusic();
                    return;
                }
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
            case 3 -> {asteroid.setSpeed(4);}
            case 2 -> {asteroid.setSpeed(3);}
            default -> {asteroid.setSpeed(2);}
            }
            asteroid.move();
            asteroid.display();
            // Asteroid trifft den Spieler
            if (asteroid.hitsPlayer(spaceship)) {
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
                    SoundFile destroySound = getSoundFile(this, "player_explotion.ogg");
                    destroySound.play();
                    gameOverScreen = true;
                    startGameOverMusic();
                    return;
                }
            }
            // Asteriod hat den Bildschirm verlassen
            if (asteroid.isOffScreen()) {
                asteroids.remove(i);
            }
        }



        // Erstellt jede Sekunde einen neuen Asteroiden an einer zufälligen X-Position
        switch (level) {
        case 3 -> enemySpawnCooldown = 800;
        case 2 -> enemySpawnCooldown = 1000;
        default -> enemySpawnCooldown = 1200;
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

        //HUD links oben
        drawHUD();

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
                   startGameMusic();
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
        if ((tutorialScreen || highscoreScreen || !gameStartScreen || gameOverScreen || VictoryScreen) &&
            !gameOverScreen && (key == 'b' || key == 'B')) {
            highscoreScreen = false;
            tutorialScreen = false;
            gameStartScreen = true;
            restartGame();
            startMenuMusic();
            return;
        }


        //Replay
        if ((gameOverScreen || VictoryScreen) && (key == 'r' || key == 'R')) {
            restartGame();
            startGameMusic();
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
        gameOverScreen = false;
        VictoryScreen = false;
        level = 1;
        bossSpawned = false;

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


    // UI & Screens Design

    public void drawHUD() {

        // Score Panel
        image(scorePanel, 90, 30, 146, 42);
        fill(255);
        textAlign(LEFT);
        textSize(14);
        text(score, 70, 30);

        // Level
        textSize(16);
        text("LEVEL " + level, 20, 75);

        // Leben als Herzen
        for (int i = 0; i < lives; i++) {
            image(heartIcon, 30 + (i * 30), 105, 26, 22);
        }

        // Boss HP
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy instanceof EnemyBoss) {
                EnemyBoss boss = (EnemyBoss) enemy;

                textAlign(CENTER);
                fill(255);
                textSize(14);
                text("GARKOS DESTROYER", width / 2, 25);

                // Hintergrund der HP-Leiste
                fill(50);
                rectMode(CENTER);
                rect(width / 2, 42, 200, 12);

                // Aktuelle HP
                float healthWidth = boss.getHealth() / 250.0f * 200;
                fill(255, 0, 0);
                rectMode(CORNER);
                rect(width / 2 - 100, 36, healthWidth, 12);
                break;
            }
        }
    }

    public void drawStartScreen() {
        textAlign(CENTER);

        // Titel
        fill(255);
        textSize(48);
        text("SPACE", width / 2, 120);
        fill(0, 180, 255);
        textSize(55);
        text("DEFENDER", width / 2, 175);

        // Untertitel
        fill(180);
        textSize(14);
        text("DEFEND THE GALAXY", width / 2, 210);

        // Menü Panel
        rectMode(CENTER);
        fill(0, 160);
        stroke(0, 180, 255);
        strokeWeight(2);
        rect(width / 2, 365, 380, 220);

        // Menü
        fill(255);
        textSize(20);
        text("[ S ]  START GAME", width / 2, 315);
        text("[ T ]  TUTORIAL", width / 2, 365);
        text("[ H ]  HIGHSCORES", width / 2, 415);

        // Hinweis
        fill(150);
        textSize(11);
        text("PRESS A KEY TO SELECT", width / 2, 500);

    }

    public void drawTutorialScreen(){
        textAlign(CENTER);

        // Titel
        fill(255);
        textSize(48);
        text("TUTORIAL", width / 2, 100);

        // Untertitel
        fill(180);
        textSize(14);
        text("PREPARE FOR THE WAR", width / 2, 145);

        // Tutorial Panel
        rectMode(CENTER);
        fill(0, 160);
        stroke(0, 180, 255);
        strokeWeight(2);
        rect(width / 2, 330, 420, 290);

        // Steuerung
        fill(255);
        textSize(20);
        text("[ A ]  MOVE LEFT", width / 2, 240);
        text("[ D ]  MOVE RIGHT", width / 2, 285);
        text("[ SPACE ]  SHOOT", width / 2, 330);

        // Spielregeln
        fill(180);
        textSize(12);
        text("DESTROY ASTEROIDS AND ENEMIES", width / 2, 390);
        text("YOU HAVE 3 LIVES", width / 2, 420);

        // Zurück
        fill(150);
        textSize(11);
        text("PRESS B TO GO BACK", width / 2, 500);
    }

    public void drawNameEntryScreen() {
        textAlign(CENTER);

        // Titel
        fill(255);
        textSize(42);
        text("IDENTIFICATION", width / 2, 100);

        // Untertitel
        fill(180);
        textSize(14);
        text("JOIN THE FEDERATION", width / 2, 145);

        // Panel
        rectMode(CENTER);
        fill(0, 160);
        stroke(0, 180, 255);
        strokeWeight(2);
        rect(width / 2, 330, 420, 220);

        // Name
        fill(180);
        textSize(12);
        text("ENTER PILOT NAME", width / 2, 270);

        fill(255);
        textSize(25);
        text(name + "_", width / 2, 330);

        // Hinweis
        fill(180);
        textSize(12);
        text("YOUR SCORE WILL BE SAVED", width / 2, 390);

        fill(150);
        textSize(11);
        text("PRESS ENTER TO START", width / 2, 500);
    }

    public void drawHighscoreScreen() {
        textAlign(CENTER);

        // Titel
        fill(255);
        textSize(48);
        text("HIGHSCORES", width / 2, 80);

        // Untertitel
        fill(180);
        textSize(14);
        text("TOP SPACE DEFENDERS", width / 2, 120);

        // Panel
        rectMode(CENTER);
        fill(0, 160);
        stroke(0, 180, 255);
        strokeWeight(2);
        rect(width / 2, 325, 460, 350);

        // Highscores
        fill(255);
        textSize(15);

        if (highscoreEntries.isEmpty()) {
            text("NO HIGHSCORES YET", width / 2, 300);
        } else {
            for (int i = 0; i < highscoreEntries.size() && i < 10; i++) {
                HighscoreEntry highscoreEntry = highscoreEntries.get(i);

                String highscoreText =
                    (i + 1) + ".  "
                        + highscoreEntry.getPlayerName()
                        + "   "
                        + highscoreEntry.getHighscoreOfPlayer();

                text(highscoreText, width / 2, 180 + (i * 28));
            }
        }

        // Zurück
        fill(150);
        textSize(11);
        text("PRESS B TO GO BACK", width / 2, 540);
    }

    public void drawGameOverScreen() {
        textAlign(CENTER);

        // Titel
        fill(255, 60, 60);
        textSize(55);
        text("GAME OVER", width / 2, 120);

        // Untertitel
        fill(180);
        textSize(14);
        text("YOUR SHIP WAS DESTROYED", width / 2, 165);

        // Panel
        rectMode(CENTER);
        fill(0, 160);
        stroke(255, 60, 60);
        strokeWeight(2);
        rect(width / 2, 330, 420, 230);

        // Score von Player
        fill(180);
        textSize(13);
        text("FINAL SCORE", width / 2, 270);
        fill(255);
        textSize(32);
        text(score, width / 2, 315);

        // Highscore
        fill(180);
        textSize(13);
        text("HIGHSCORE", width / 2, 365);
        fill(255);
        textSize(20);
        if (!highscoreEntries.isEmpty()) {
            text(highscoreEntries.getFirst().getHighscoreOfPlayer(),
                width / 2, 400);
        }

        // Replay
        fill(150);
        textSize(11);
        text("PRESS R TO REPLAY OR B TO MENU", width / 2, 510);

    }

    public void drawVictoryScreen() {
        textAlign(CENTER);

        // Titel
        fill(0, 255, 0);
        textSize(55);
        text("Victroy!", width / 2, 120);

        // Untertitel
        fill(180);
        textSize(14);
        text("YOU SAVED THE GALAXY", width / 2, 165);

        // Panel
        rectMode(CENTER);
        fill(0, 160);
        stroke(0, 255, 0);
        strokeWeight(2);
        rect(width / 2, 330, 420, 230);

        // Score von Player
        fill(180);
        textSize(13);
        text("FINAL SCORE", width / 2, 270);
        fill(255);
        textSize(32);
        text(score, width / 2, 315);

        // Highscore
        fill(180);
        textSize(13);
        text("HIGHSCORE", width / 2, 365);
        fill(255);
        textSize(20);
        if (!highscoreEntries.isEmpty()) {
            text(highscoreEntries.getFirst().getHighscoreOfPlayer(),
                width / 2, 400);
        }

        // Replay
        fill(150);
        textSize(11);
        text("PRESS R TO REPLAY OR B TO MENU", width / 2, 510);

    }

    public void startGameMusic() {
        menuMusic.stop();
        gameOverMusic.stop();
        VictoryMusic.stop();
        if (!gameMusic.isPlaying()) {
            gameMusic.loop();
        }
    }

    public void startGameOverMusic(){
        menuMusic.stop();
        gameMusic.stop();
        VictoryMusic.stop();
        if (!gameOverMusic.isPlaying()) {
            gameOverMusic.loop();
        }
    }

    public void startVictoryMusic(){
        menuMusic.stop();
        gameMusic.stop();
        gameOverMusic.stop();
        if (!VictoryMusic.isPlaying()) {
            VictoryMusic.loop();
        }
    }

    public void startMenuMusic(){
        gameOverMusic.stop();
        gameMusic.stop();
        VictoryMusic.stop();
        if (!menuMusic.isPlaying()) {
            menuMusic.loop();
        }
    }

}