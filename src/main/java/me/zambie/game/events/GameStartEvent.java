package me.zambie.game.events;

import me.zambie.game.Arena;
import me.zambie.game.Game;
import me.zambie.game.GameEvent;
import me.zambie.game.GamePhase;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Set;

public class GameStartEvent implements GameEvent  {

    private final boolean lobbyActive;

    public GameStartEvent(boolean lobbyActive) {
        this.lobbyActive = lobbyActive;
    }

    public boolean isLobbyActive() {
        return lobbyActive;
    }

    @Override
    public int getTime() {
        return 10;
    }

    @Override
    public void execute(Game game) {
        Set<Player> playerSet = game.getPlayerSet();
        Arena arena = game.getArena();

        Vector spawnVector = arena.getSpawn();
        if (lobbyActive){
            for (Player player : playerSet) {
                player.sendTitle("", "", 0, 20, 0);

                player.teleport(new Location(arena.getWorld(), spawnVector.getX(), spawnVector.getY(), spawnVector.getZ()));
                game.setPlayer(player, game.getTeam("playing"));
            }
            game.setRunning(false);
            game.setDuration(0);
            game.setPhase(GamePhase.RUNNING);
        }else {
            for (Player player : playerSet) {
                player.sendTitle("§a§lGO", "", 0, 20, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_GUITAR, 1F, 1F);
            }
            game.setRunning(true);
            game.setPhase(GamePhase.RUNNING);
        }
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
