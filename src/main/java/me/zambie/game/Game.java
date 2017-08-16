package me.zambie.game;

import me.zambie.game.events.GamePhaseChangeEvent;
import me.zambie.game.events.GameStartEvent;
import me.zambie.game.events.GameTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Game {

    private final Queue<GameEvent> eventQueue = new PriorityQueue<>();
    private final Set<BlockState> blockStateSet = new HashSet<>();
    private Scoreboard scoreboard;

    private Arena arena;
    private GamePhase phase = GamePhase.WAITING;

    private int time = 10;
    private int dur = 0;
    private boolean running = false;

    public Game(GameCore gameCore, ScoreboardManager scoreboardManager) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        Team lobbyTeam = scoreboard.registerNewTeam("lobby");
        lobbyTeam.setAllowFriendlyFire(false);
        lobbyTeam.setCanSeeFriendlyInvisibles(true);
        lobbyTeam.setColor(ChatColor.BLUE);

        Team playingTeam = scoreboard.registerNewTeam("playing");
        playingTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        playingTeam.setAllowFriendlyFire(false);
        playingTeam.setPrefix("§a");
        playingTeam.setColor(ChatColor.GREEN);

        Team spectatorTeam = scoreboard.registerNewTeam("spectator");
        spectatorTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        spectatorTeam.setAllowFriendlyFire(false);
        spectatorTeam.setCanSeeFriendlyInvisibles(true);
        spectatorTeam.setColor(ChatColor.DARK_GRAY);

        getEventQueue().offer(new GameStartEvent(true));
        getEventQueue().offer(new GameStartEvent(false));

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new GameTickEvent(Game.this));
            }
        }.runTaskTimer(gameCore, 0L, 20L);

        this.scoreboard = scoreboard;
    }

    public void broadcastMessage(String string){
        for (Player player : getArena().getWorld().getPlayers()){
            player.sendMessage(string);
        }
    }

    public Queue<GameEvent> getEventQueue() {
        return eventQueue;
    }

    public GameEvent getNextEvent(){
        return getEventQueue().peek();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Set<Player> getPlayerSet(){
        Set<Player> playerSet = new HashSet<>();
        if (scoreboard != null){
            getScoreboard().getTeams().forEach(team -> team.getEntries()
                    .forEach(s -> playerSet.add(Bukkit.getPlayer(s))));
        }
        return playerSet;
    }

    public Team getTeam(String string){
        return getScoreboard().getTeam(string);
    }

    public void setPlayer(Player player, Team team){
        for (Team t : getScoreboard().getTeams()){
            if (t.hasEntry(player.getName())){
                t.removeEntry(player.getName());
            }
        }
        team.addEntry(player.getName());
    }

    public void removePlayer(Player player){
        Set<Team> teamSet = getScoreboard().getTeams();
        for (Team team : teamSet){
            if (!team.getEntries().contains(player.getName()))continue;
            team.removeEntry(player.getName());
        }
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
        Bukkit.getPluginManager().callEvent(new GamePhaseChangeEvent(this, phase));
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setDuration(int dur) {
        this.dur = dur;
    }

    public int getDuration(){
        return dur;
    }

    public void addBlock(BlockState blockState){
        for (BlockState state : blockStateSet){
            if (state.getLocation().equals(blockState)){
                return;
            }
        }
        blockStateSet.add(blockState);
    }

    public void restore(){
        for (BlockState blockState : blockStateSet){
            blockState.update(true, false);
        }
        blockStateSet.clear();

        for (Player player : arena.getWorld().getPlayers()){
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            for (PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }
        }

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        arena.setRunning(false);
    }
}