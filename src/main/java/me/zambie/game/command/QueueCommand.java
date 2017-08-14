package me.zambie.game.command;

import me.zambie.game.Arena;
import me.zambie.game.Game;
import me.zambie.game.GameCore;
import me.zambie.game.events.GameJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class QueueCommand extends SubCommand {

    QueueCommand(GameCore gameCore) {
        super(gameCore, "queue");
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String string, String[] strings)
            throws CommandException {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage("This is a player only command");
            return;
        }

        Player player = (Player) commandSender;

        Optional<Game> gameOptional = GameCore.getAvailableGame();
        Game game = gameOptional.orElseGet(getGameCore()::initiateGame);

        if (game != null) {
            Arena arena = game.getArena();
            Location location = new Location(arena.getWorld(), arena.getSpawn().getX(), arena.getSpawn().getY(), arena.getSpawn().getZ());

            player.sendMessage(String.format("§aSending you to %s", arena.getName()));
            game.setPlayer(player, game.getTeam("lobby"));
            player.teleport(location);

            Bukkit.getPluginManager().callEvent(new GameJoinEvent(player, game));
        }else {
            player.sendMessage("§cThere are not games available right now");
        }
    }
}
