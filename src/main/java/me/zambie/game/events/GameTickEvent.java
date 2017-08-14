package me.zambie.game.events;

import me.zambie.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTickEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    private final Game game;

    public GameTickEvent(Game game) {
        this.game = game;
    }

    private static HandlerList getHandlerList() {
        return handlerList;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
