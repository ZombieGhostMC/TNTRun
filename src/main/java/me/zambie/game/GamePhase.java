package me.zambie.game;

public enum GamePhase {
    WAITING(new String[]{
            "  ",
            "Players: ",
            "   ",
            " ",
            "    ",
            "Map: ",
            "     ",
            "§ewww.hypixel"}),
    RUNNING(new String[]{
            "§7Duration: ",
            "  ",
            "Double Jump: ",
            "   ",
            "Players Alive: ",
            "    ",
            " ",
            "§ewww.hypixel"}),
    END(new String[]{
            "§7Duration: ",
            "  ",
            "Double Jump: ",
            "   ",
            "Players Alive: ",
            "    ",
            " ",
            "§ewww.hypixel"});

    private String[] scores;

    GamePhase(String[] scores) {
        this.scores = scores;
    }

    public String[] getScores() {
        return scores;
    }
}
