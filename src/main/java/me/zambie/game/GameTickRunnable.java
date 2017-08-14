package me.zambie.game;

import me.zambie.game.events.GameTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTickRunnable extends BukkitRunnable {

    private final Game game;

    public GameTickRunnable(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        Bukkit.getPluginManager().callEvent(new GameTickEvent(getGame()));
    }

    public Game getGame() {
        return game;
    }
}
