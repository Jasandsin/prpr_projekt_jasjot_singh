package spacedefender;

public class HighscoreEntry {

    private  String playerName;
    private int highscoreOfPlayer;

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

    public void setHighscoreOfPlayer(int highscoreOfPlayer) {
        this.highscoreOfPlayer = highscoreOfPlayer;
    }
}
