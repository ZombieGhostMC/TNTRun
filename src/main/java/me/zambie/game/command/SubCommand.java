package me.zambie.game.command;

import me.zambie.game.GameCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    private final GameCore gameCore;
    private final String command;

    SubCommand(GameCore gameCore, String command) {
        this.gameCore = gameCore;
        this.command = command;
    }

    String getCommand() {
        return command;
    }

    public GameCore getGameCore() {
        return gameCore;
    }

    public abstract void execute(CommandSender commandSender, Command command, String string, String[] strings)
            throws CommandException;
}
