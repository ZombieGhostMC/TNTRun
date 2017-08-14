package me.zambie.game.command;

import me.zambie.game.GameCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GameCommand implements CommandExecutor {

    private static final Set<SubCommand> commandSet = new HashSet<>();

    public GameCommand(GameCore gameCore) {
        commandSet.add(new QueueCommand(gameCore));
        commandSet.add(new ArenaCreateCommand(gameCore));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length <= 0){
            commandSender.sendMessage("§cInsufficient arguments");
            return true;
        }

        String argument = strings[0];
        Optional<SubCommand> commandOptional = getCommandSet().stream().filter(subCommand ->
                subCommand.getCommand().equalsIgnoreCase(argument)).findFirst();

        if (!commandOptional.isPresent()){
            commandSender.sendMessage("§cInvalid argument");
            return true;
        }

        try {
            commandOptional.get().execute(commandSender, command, s, strings);
        } catch (CommandException e) {
            commandSender.sendMessage(String.format("§c%s", e.getMessage()));
        }
        return true;
    }

    private static Set<SubCommand> getCommandSet() {
        return commandSet;
    }

}
