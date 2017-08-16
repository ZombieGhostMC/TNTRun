package me.zambie.game.events;

import me.zambie.game.Game;
import me.zambie.game.GameEvent;
import me.zambie.game.GamePhase;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class GameEndEvent implements GameEvent {

    private final JavaPlugin plugin;

    public GameEndEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getTime() {
        return 10;
    }

    @Override
    public void execute(Game game) {
        for (Player player : game.getPlayerSet()) {
            Optional<String> winner = game.getTeam("playing").getEntries().stream().findFirst();
            if (winner.isPresent()) {
                player.sendTitle(String.format("§a%s", winner.get()), "§ewon!", 20, 200, 20);
            }else {
                player.sendTitle("§cNot a single person won", "§7:(", 20, 200, 20);
            }
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 1, 1);
        }
        game.setRunning(false);
        new BukkitRunnable() {
            @Override
            public void run() {
                game.restore();
            }
        }.runTaskLater(plugin, 200L);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
