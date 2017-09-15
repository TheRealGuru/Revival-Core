package gg.revival.core.essentials;

import gg.revival.core.Revival;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ECommandExecutor implements CommandExecutor {

    /**
     * Loops through every registered command to see if it matches
     * @param sender CommandSender
     * @param command Command
     * @param commandLabel String
     * @param args Arguments
     * @return NOTHIN HEHE
     */
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[]) {
        for(ECommand cmd : Revival.getCommandManager().getCommands()) {
            if(command.getName().equalsIgnoreCase(cmd.getLabel())) {
                cmd.onCommand(sender, args);

                return true;
            }
        }

        return false;
    }

}
