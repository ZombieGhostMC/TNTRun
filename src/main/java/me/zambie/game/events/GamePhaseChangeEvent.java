package me.zambie.game.events;

import me.zambie.game.Game;
import me.zambie.game.GamePhase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GamePhaseChangeEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    private final Game game;
    private final GamePhase newPhase;

    public GamePhaseChangeEvent(Game game, GamePhase newPhase) {
        this.game = game;
        this.newPhase = newPhase;
    }

    public Game getGame() {
        return game;
    }

    public GamePhase getNewPhase() {
        return newPhase;
    }

    private static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
