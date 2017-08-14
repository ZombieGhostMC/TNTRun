package me.zambie.game;

import me.zambie.game.command.GameCommand;
import me.zambie.game.listener.GameListener;
import me.zambie.game.listener.SignListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GameCore extends JavaPlugin {

    private static final Set<Game> gameSet = new HashSet<>();

    @Override
    public void onEnable() {
        registerListener();
        registerCommand();

        try {
            Arena.load(getDataFolder());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Failed to read arena.json, server shutdown");
            Bukkit.shutdown();
        }
    }

    @Override
    public void onDisable() {
        Arena.save(getDataFolder());
    }

    private void registerCommand(){
        getCommand("game").setExecutor(new GameCommand(this));
    }

    private void registerListener(){
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(this), this);
    }

    public static Optional<Game> getAvailableGame(){
        return gameSet.stream().filter(game -> !game.isRunning()).findAny();
    }

    public Game initiateGame(){
        Game game = new Game(GameCore.this, Bukkit.getScoreboardManager());
        Optional<Arena> arenaOptional = Arena.get();
        if (arenaOptional.isPresent()) {
            game.setArena(arenaOptional.get());
            gameSet.add(game);
            return game;
        }
        return null;
    }
}