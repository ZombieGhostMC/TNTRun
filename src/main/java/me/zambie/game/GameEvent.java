package me.zambie.game;

public interface GameEvent extends Comparable{
    int getTime();
    void execute(Game game);
}