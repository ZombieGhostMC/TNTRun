package me.zambie.game.events;

import me.zambie.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameJoinEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Game game;

    public GameJoinEvent(Player player, Game game) {
        this.player = player;
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
