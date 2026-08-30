package spacedefender;

public class HighscoreEntry {

    private final String playerName;
    private final int highscoreOfPlayer;

    public HighscoreEntry(String PlayerName, int highscoreOfPlayer){
        this.playerName = PlayerName;
        this.highscoreOfPlayer = highscoreOfPlayer;
    }

    public int getHighscoreOfPlayer() {
        return highscoreOfPlayer;
    }

    public String getPlayerName() {
        return playerName;
    }

}
