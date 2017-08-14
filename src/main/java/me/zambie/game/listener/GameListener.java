package me.zambie.game.listener;

import me.zambie.game.*;
import me.zambie.game.events.GameJoinEvent;
import me.zambie.game.events.GamePhaseChangeEvent;
import me.zambie.game.events.GameTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.util.List;

public class GameListener implements Listener {

    private final GameCore gameCore;
    private final String[] strings = new String [] {
            "§e§lTNT RUN",
            "§e§lTNT RUN",
            "§e§lTNT RUN",
            "§e§lTNT RUN",
            "§e§lTNT RUN",
            "§e§lTNT RUN",
            "§e§lTNT RUN",
            "§e§lTNT RUN",
            "§e§lTNT RUN",
            "§e§lTNT RUN",

            "§6§lT§e§lNT RUN",
            "§f§lT§6§lN§e§lT RUN",
            "§f§lTN§6§lT §e§lRUN",
            "§f§lTNT §6§lR§e§lUN",
            "§f§lTNT R§6§lU§e§lN",
            "§f§lTNT RU§6§lN",

            "§e§lTNT RUN",
            "§f§lTNT RUN",
            "§e§lTNT RUN",
            "§f§lTNT RUN",
            "§e§lTNT RUN",
            "§f§lTNT RUN",
            "§e§lTNT RUN",
            "§f§lTNT RUN",
            "§e§lTNT RUN",
            "§f§lTNT RUN",
            "§e§lTNT RUN",
            "§f§lTNT RUN",
    };
    private int i = 0;

    public GameListener(GameCore gameCore) {
        this.gameCore = gameCore;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTick(GameTickEvent event){
        Game game = event.getGame();
        List<Player> playerList = game.getArena().getWorld().getPlayers();

        for (Player player : playerList){
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard == null){
                scoreboard = game.getScoreboard();
            }

            Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
            if (objective == null){
                objective = scoreboard.registerNewObjective("game", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName("preScoreboard");

                int length = game.getPhase().getScores().length;
                for (String s : game.getPhase().getScores()){
                    objective.getScore(s).setScore(length--);

                    Team team = scoreboard.getTeam(s);
                    if (team == null){
                        team = scoreboard.registerNewTeam(s);
                        team.addEntry(s);
                    }
                }
            }

            String string = strings[i];
            i = i >= strings.length - 1 ? 0 : i + 1;

            objective.setDisplayName(string);
            refreshScoreboard(game.getPhase(), scoreboard, game);
            player.setScoreboard(scoreboard);
        }

        if (game.isRunning() && game.getPhase().equals(GamePhase.RUNNING)) {
            for (String string : game.getTeam("playing").getEntries()) {
                Player player = Bukkit.getPlayer(string);
                if (game.getPhase().equals(GamePhase.RUNNING)) {
                    Block block1 = player.getLocation().clone().subtract(0, 1, 0).getBlock();
                    Block block2 = block1.getLocation().clone().subtract(0, 1, 0).getBlock();

                    game.addBlock(block1.getState());
                    game.addBlock(block2.getState());

                    block1.setType(Material.AIR);
                    block2.setType(Material.AIR);
                }
            }
        }

        if (game.getPhase().equals(GamePhase.WAITING) && !game.isRunning()){
            game.setTime(10);
        }else {
            game.setTime(game.getTime() - 1);
        }

        if (game.getPhase().equals(GamePhase.RUNNING)
                && game.getTeam("playing").getSize() == 1){
            game.setPhase(GamePhase.END);
        }

        if (game.getTime() <= 0) {
            GameEvent gameEvent = game.getEventQueue().poll();
            if (gameEvent != null) {
                gameEvent.execute(game);
                game.setTime(gameEvent.getTime());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPhaseChange(GamePhaseChangeEvent event){
        Game game = event.getGame();
        GamePhase phase = event.getNewPhase();
        List<Player> playerList = game.getArena().getWorld().getPlayers();

        for (Player player : playerList){
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard == null){
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            }

            Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
            if (objective != null){
                objective.unregister();
            }

            objective = scoreboard.registerNewObjective(phase.name(), "dummy");
            int length = phase.getScores().length;
            for (String s : phase.getScores()){
                objective.getScore(s).setScore(length--);

                Team team = scoreboard.getTeam(s);
                if (team == null){
                    team = scoreboard.registerNewTeam(s);
                    team.addEntry(s);
                }
            }

            refreshScoreboard(game.getPhase(), scoreboard, game);
            player.setScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onGameJoin(GameJoinEvent event){
        Game game = event.getGame();
        if (game.getArena().isRunning()){
            return;
        }

        Arena arena = game.getArena();
        if (arena.getWorld().getPlayers().size() != 1){
            return;
        }
        game.setRunning(true);
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        Bukkit.getOnlinePlayers().forEach(p -> p.teleport(Bukkit.getWorld("world").getSpawnLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event){
        Player player = event.getPlayer();
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event){
        event.setCancelled(true);
    }

    private void refreshScoreboard(GamePhase phase, Scoreboard scoreboard, Game game){
        switch (phase) {
            case WAITING:{
                Team playerCountTeam = scoreboard.getTeam("Players: ");
                if (playerCountTeam != null) {
                    playerCountTeam.setSuffix(String.format("§a%s/%s", game.getArena().getWorld().getPlayers().size(), "12"));
                }

                Team timerTeam = scoreboard.getTeam(" ");
                if (timerTeam != null) {
                    timerTeam.setPrefix(game.isRunning() ? "Starting:" : "Waiting...");
                    timerTeam.setSuffix(game.isRunning() ? String.format("§a%ss", game.getTime()) : "");
                }

                Team mapTeam = scoreboard.getTeam("Map: ");
                if (mapTeam != null) {
                    mapTeam.setSuffix(String.format("§a%s", game.getArena().getName()));
                }

                Team websiteTeam = scoreboard.getTeam("§ewww.hypixel");
                if (websiteTeam != null) {
                    websiteTeam.setSuffix(".net");
                }

                break;
            }
            case RUNNING :
            case END:{
                Team durationTeam = scoreboard.getTeam("§7Duration: ");
                if (durationTeam != null) {
                    durationTeam.setSuffix(String.format("%ss", game.getDuration()));
                }

                Team jumpTeam = scoreboard.getTeam("Double Jump: ");
                if (jumpTeam != null) {
                    jumpTeam.setSuffix("§a0§7/1");
                }

                Team countTeam = scoreboard.getTeam("Players Alive: ");
                if (countTeam != null) {
                    countTeam.setSuffix(String.format("§a%s", game.getTeam("playing").getSize()));
                }

                Team dateTeam = scoreboard.getTeam(" ");
                if (dateTeam != null) {
                    dateTeam.setPrefix("");
                    dateTeam.setSuffix(String.format("§7%s", "8/14/2017"));
                }

                Team websiteTeam = scoreboard.getTeam("§ewww.hypixel");
                if (websiteTeam != null) {
                    websiteTeam.setSuffix(".net");
                }
                break;
            }
        }
    }

    public GameCore getGameCore() {
        return gameCore;
    }
}
