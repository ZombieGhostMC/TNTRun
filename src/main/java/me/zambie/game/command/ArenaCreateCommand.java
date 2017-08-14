package me.zambie.game.command;

import me.zambie.game.Arena;
import me.zambie.game.GameCore;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class ArenaCreateCommand extends SubCommand {

    ArenaCreateCommand(GameCore gameCore) {
        super(gameCore, "arenacreate");
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String string, String[] strings)
            throws CommandException {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage("This is a player only command");
            return;
        }

        Player player = (Player) commandSender;

        if (string.length() < 1){
            commandSender.sendMessage("§c/game arenacreate [name]");
            return;
        }

        String name = strings[1];
        World world = player.getWorld();
        Vector spawn = player.getLocation().toVector();

        Arena.getSet().add(new Arena(name, world.getName(), spawn));
        commandSender.sendMessage(String.format("§eAdded arena \"%s\" in world \"%s\" to the set", name, world.getName()));
    }
}
